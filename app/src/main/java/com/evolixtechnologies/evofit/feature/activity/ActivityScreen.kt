package com.evolixtechnologies.evofit.feature.activity

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.evolixtechnologies.evofit.core.designsystem.EvoCard
import com.evolixtechnologies.evofit.core.designsystem.EvoGreen
import com.evolixtechnologies.evofit.core.designsystem.EvoMuted
import kotlinx.coroutines.delay
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

private enum class Metric(val label: String, val unit: String) {
    STEPS("Steps", "steps"), DISTANCE("Distance", "km"), CALORIES("Calories", "kcal"), TIME("Active time", "min")
}

private data class DailyRecord(val date: LocalDate, val steps: Int, val stepGoal: Int) {
    val distance get() = steps * 0.00078
    val calories get() = steps * 0.0445
    val minutes get() = steps / 119.0
    fun value(metric: Metric): Double = when (metric) {
        Metric.STEPS -> steps.toDouble()
        Metric.DISTANCE -> distance
        Metric.CALORIES -> calories
        Metric.TIME -> minutes
    }
}

private fun readRecord(context: Context, date: LocalDate, today: LocalDate, currentSteps: Int, goal: Int): DailyRecord {
    val prefs = context.getSharedPreferences("evofit_steps", Context.MODE_PRIVATE)
    return DailyRecord(date, if (date == today) currentSteps else prefs.getInt("steps_$date", 0),
        prefs.getInt("goal_$date", goal).coerceAtLeast(1))
}

private fun valueText(value: Double, metric: Metric): String = when (metric) {
    Metric.STEPS -> "%,d".format(value.roundToInt())
    Metric.DISTANCE -> "%.2f".format(value)
    Metric.CALORIES, Metric.TIME -> "%.0f".format(value)
}

@Composable
fun ActivityScreen(
    steps: Int,
    stepGoal: Int,
    distanceGoal: String,
    activeGoal: String,
    caloriesGoal: String,
    onEditGoals: () -> Unit,
    onHistory: () -> Unit
) {
    val context = LocalContext.current
    val today = LocalDate.now()
    var metric by remember { mutableStateOf(Metric.STEPS) }
    var period by remember { mutableIntStateOf(0) }
    var anchor by remember { mutableStateOf(today) }
    var menuOpen by remember { mutableStateOf(false) }
    var showDays by remember { mutableStateOf(false) }
    var refresh by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) { while (true) { delay(15_000); refresh++ } }

    val start = when (period) { 0 -> anchor; 1 -> anchor.with(DayOfWeek.MONDAY); else -> anchor.withDayOfMonth(1) }
    val end = when (period) { 0 -> start; 1 -> start.plusDays(6); else -> start.plusMonths(1).minusDays(1) }
    fun records(from: LocalDate, to: LocalDate) = generateSequence(from) { if (it < to) it.plusDays(1) else null }
        .map { readRecord(context, it, today, steps, stepGoal) }.toList()
    val days = remember(start, end, steps, stepGoal, refresh) { records(start, end) }
    val previousStart = when (period) { 0 -> start.minusDays(1); 1 -> start.minusWeeks(1); else -> start.minusMonths(1) }
    val previousEnd = when (period) { 0 -> previousStart; 1 -> previousStart.plusDays(6); else -> previousStart.plusMonths(1).minusDays(1) }
    val previous = remember(previousStart, previousEnd, refresh) { records(previousStart, previousEnd) }
    val visibleDays = days.filter { !it.date.isAfter(today) }
    val total = visibleDays.sumOf { it.value(metric) }
    val average = if (visibleDays.isEmpty()) 0.0 else total / visibleDays.size
    val delta = total - previous.sumOf { it.value(metric) }
    val goal = when (metric) {
        Metric.STEPS -> if (period == 0) (days.firstOrNull()?.stepGoal ?: stepGoal).toDouble() else stepGoal.toDouble()
        Metric.DISTANCE -> distanceGoal.toDoubleOrNull() ?: 0.0
        Metric.CALORIES -> caloriesGoal.toDoubleOrNull() ?: 0.0
        Metric.TIME -> activeGoal.toDoubleOrNull() ?: 0.0
    }
    val day = days.firstOrNull() ?: DailyRecord(anchor, 0, stepGoal)
    val canGoForward = when (period) {
        0 -> anchor < today
        1 -> start.plusWeeks(1) <= today
        else -> YearMonth.from(anchor) < YearMonth.from(today)
    }

    Column(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()).padding(20.dp).padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.weight(1f)) {
                Row(Modifier.clickable { menuOpen = true }, verticalAlignment = Alignment.CenterVertically) {
                    Text(metric.label, style = MaterialTheme.typography.titleLarge)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Choose metric")
                }
                DropdownMenu(menuOpen, onDismissRequest = { menuOpen = false }) {
                    Metric.entries.forEach { choice ->
                        DropdownMenuItem(text = { Text(choice.label) }, onClick = { metric = choice; menuOpen = false })
                    }
                }
            }
            Text("${streak(context, today, steps, stepGoal)} day streak",
                color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("Day", "Week", "Month").forEachIndexed { index, title ->
                Surface(onClick = { period = index; anchor = today; showDays = false },
                    modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp),
                    color = if (period == index) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface) {
                    Text(title, Modifier.padding(vertical = 12.dp), textAlign = TextAlign.Center,
                        color = if (period == index) MaterialTheme.colorScheme.onPrimaryContainer else EvoMuted,
                        fontWeight = if (period == index) FontWeight.Bold else FontWeight.Medium)
                }
            }
        }
        EvoCard {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    IconButton(onClick = { anchor = when (period) { 0 -> anchor.minusDays(1); 1 -> anchor.minusWeeks(1); else -> anchor.minusMonths(1) } }) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Previous period")
                    }
                    Text(periodLabel(start, end, period, today), style = MaterialTheme.typography.titleSmall)
                    IconButton(onClick = { anchor = when (period) { 0 -> anchor.plusDays(1); 1 -> anchor.plusWeeks(1); else -> anchor.plusMonths(1) } },
                        enabled = canGoForward) { Icon(Icons.Default.ChevronRight, contentDescription = "Next period") }
                }
                if (period == 0) {
                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(valueText(day.value(metric), metric), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                        Text(metric.unit, style = MaterialTheme.typography.bodyMedium, color = EvoMuted)
                        Row(Modifier.clickable(onClick = onEditGoals).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(if (goal > 0) "Goal ${valueText(goal, metric)} ${metric.unit}" else "Set a goal",
                                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                            Icon(Icons.Default.Edit, contentDescription = "Edit goals", modifier = Modifier.padding(start = 8.dp).size(16.dp),
                                tint = MaterialTheme.colorScheme.primary)
                        }
                        LinearProgressIndicator(progress = { if (goal > 0) (day.value(metric) / goal).toFloat().coerceIn(0f, 1f) else 0f },
                            modifier = Modifier.fillMaxWidth().height(9.dp), color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant)
                    }
                    Text("Hourly activity", style = MaterialTheme.typography.titleSmall)
                    val hourly = remember(anchor, steps, refresh, metric) {
                        val prefs = context.getSharedPreferences("evofit_steps", Context.MODE_PRIVATE)
                        (0..23).map { hour ->
                            DailyRecord(anchor, prefs.getInt("hour_${anchor}_$hour", 0), stepGoal).value(metric)
                        }
                    }
                    ActivityBars(hourly, Modifier.fillMaxWidth().height(125.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        listOf("00", "06", "12", "18", "24").forEach {
                            Text(it, style = MaterialTheme.typography.bodySmall, color = EvoMuted)
                        }
                    }
                    if (hourly.sum() == 0.0 && day.steps > 0) {
                        Text("Hourly detail is available for newly recorded steps.", style = MaterialTheme.typography.bodySmall, color = EvoMuted)
                    }
                    HorizontalDivider()
                    DayDetails(day)
                } else {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        SummaryValue("Average", valueText(average, metric), metric.unit)
                        SummaryValue("Total", valueText(total, metric), metric.unit)
                    }
                    ActivityBars(days.map { it.value(metric) }, Modifier.fillMaxWidth().height(150.dp)) { index ->
                        days.getOrNull(index)?.takeIf { !it.date.isAfter(today) }?.let {
                            anchor = it.date
                            period = 0
                        }
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        val labels = if (period == 1) days.map { it.date.dayOfWeek.name.take(1) }
                            else listOf("1", "7", "14", "21", end.dayOfMonth.toString())
                        labels.forEach { Text(it, style = MaterialTheme.typography.bodySmall, color = EvoMuted) }
                    }
                    Text("${visibleDays.count { (if (metric == Metric.STEPS) it.stepGoal.toDouble() else goal) > 0 && it.value(metric) >= (if (metric == Metric.STEPS) it.stepGoal.toDouble() else goal) }} goal days in this period",
                        style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                    Surface(onClick = { showDays = !showDays }, modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                        Text(if (showDays) "Hide daily data" else "Show all data", Modifier.padding(14.dp),
                            style = MaterialTheme.typography.labelLarge)
                    }
                    if (showDays) visibleDays.asReversed().forEach { record ->
                        Row(Modifier.fillMaxWidth().clickable { anchor = record.date; period = 0 }.padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(record.date.format(DateTimeFormatter.ofPattern("EEE, d MMM")), style = MaterialTheme.typography.bodyMedium)
                            Text("${valueText(record.value(metric), metric)} ${metric.unit}",
                                style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        if (period != 0) {
            Text(if (period == 1) "Compared with previous week" else "Compared with previous month",
                style = MaterialTheme.typography.titleSmall)
            EvoCard {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("${if (delta >= 0) "+" else ""}${valueText(delta, metric)} ${metric.unit}",
                        color = if (delta >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.titleLarge)
                    Text(if (previous.all { it.steps == 0 }) "No activity recorded in the previous period"
                        else if (delta >= 0) "More active than the previous period" else "Less active than the previous period",
                        style = MaterialTheme.typography.bodyMedium, color = EvoMuted)
                    visibleDays.maxByOrNull { it.steps }?.takeIf { it.steps > 0 }?.let {
                        Text("Most active day: ${it.date.format(DateTimeFormatter.ofPattern("EEEE"))}",
                            style = MaterialTheme.typography.bodyMedium)
                    }
                    val savedSteps = context.getSharedPreferences("evofit_steps", Context.MODE_PRIVATE)
                    val hourlyTotals = (0..23).map { hour ->
                        visibleDays.sumOf { savedSteps.getInt("hour_${it.date}_$hour", 0) }
                    }
                    val busiestHour = hourlyTotals.indices.maxByOrNull { hourlyTotals[it] }
                    if (busiestHour != null && hourlyTotals[busiestHour] > 0) {
                        Text("Most active hour: ${"%02d:00".format(busiestHour)} - ${"%02d:00".format((busiestHour + 1) % 24)}",
                            style = MaterialTheme.typography.bodyMedium)
                    }
                    HorizontalDivider()
                    Text("Distance ${"%.2f".format(visibleDays.sumOf { it.distance })} km  |  Calories ${"%.0f".format(visibleDays.sumOf { it.calories })} kcal  |  Active ${"%.0f".format(visibleDays.sumOf { it.minutes })} min",
                        style = MaterialTheme.typography.bodySmall, color = EvoMuted)
                }
            }
        }
        Surface(onClick = onHistory, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.primaryContainer) {
            Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text("Daily tracking history", style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer)
                Icon(Icons.Default.ChevronRight, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
        EvoCard {
            Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val achieved = achievedDays(context, today, steps, stepGoal)
                Text("100 goal days", style = MaterialTheme.typography.titleSmall)
                Text("$achieved of 100 days", style = MaterialTheme.typography.bodyMedium, color = EvoMuted)
                LinearProgressIndicator(progress = { (achieved / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant)
            }
        }
    }
}

private fun periodLabel(start: LocalDate, end: LocalDate, period: Int, today: LocalDate) = when (period) {
    0 -> if (start == today) "Today" else start.format(DateTimeFormatter.ofPattern("EEE, d MMM yyyy"))
    1 -> "${start.format(DateTimeFormatter.ofPattern("d MMM"))} - ${end.format(DateTimeFormatter.ofPattern("d MMM"))}"
    else -> start.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
}

private fun achievedDays(context: Context, today: LocalDate, steps: Int, goal: Int): Int {
    val prefs = context.getSharedPreferences("evofit_steps", Context.MODE_PRIVATE)
    val dates = prefs.all.keys.mapNotNull { key ->
        if (!key.startsWith("steps_")) null
        else runCatching { LocalDate.parse(key.removePrefix("steps_")) }.getOrNull()
    }.filter { !it.isAfter(today) }.toSet() + today
    return dates.count { date ->
        val saved = if (date == today) steps else prefs.getInt("steps_$date", 0)
        saved >= prefs.getInt("goal_$date", goal).coerceAtLeast(1)
    }
}

private fun streak(context: Context, today: LocalDate, steps: Int, goal: Int): Int {
    val prefs = context.getSharedPreferences("evofit_steps", Context.MODE_PRIVATE)
    var count = 0
    var offset = if (steps >= goal) 0 else 1
    while (offset < 3650) {
        val date = today.minusDays(offset.toLong())
        val saved = if (offset == 0) steps else prefs.getInt("steps_$date", 0)
        if (saved < prefs.getInt("goal_$date", goal).coerceAtLeast(1)) break
        count++
        offset++
    }
    return count
}

@Composable
private fun SummaryValue(label: String, value: String, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge)
        Text("$label | $unit", style = MaterialTheme.typography.bodySmall, color = EvoMuted)
    }
}

@Composable
private fun DayDetails(record: DailyRecord) {
    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        listOf(
            "Steps" to "%,d".format(record.steps),
            "Distance" to "%.2f km".format(record.distance),
            "Calories" to "%.0f kcal".format(record.calories),
            "Active time" to "%.0f min".format(record.minutes)
        ).forEach { (label, value) ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(label, style = MaterialTheme.typography.bodyMedium, color = EvoMuted)
                Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ActivityBars(values: List<Double>, modifier: Modifier, onBarTap: ((Int) -> Unit)? = null) {
    val emptyColor = MaterialTheme.colorScheme.surfaceVariant
    val barColor = MaterialTheme.colorScheme.primary
    val tapModifier = if (onBarTap == null) Modifier else Modifier.pointerInput(values) {
        detectTapGestures { position ->
            if (size.width > 0 && values.isNotEmpty()) {
                onBarTap(((position.x / size.width) * values.size).toInt().coerceIn(0, values.lastIndex))
            }
        }
    }
    Canvas(modifier.then(tapModifier)) {
        val maximum = (values.maxOrNull() ?: 0.0).coerceAtLeast(1.0)
        val slot = size.width / values.size.coerceAtLeast(1)
        val width = (slot * .55f).coerceAtLeast(2f)
        drawLine(emptyColor, Offset(0f, size.height), Offset(size.width, size.height), strokeWidth = 1f)
        values.forEachIndexed { index, value ->
            val height = if (value == 0.0) 2f else (size.height * (value / maximum).toFloat() * .86f).coerceAtLeast(4f)
            drawRoundRect(if (value > 0) barColor else emptyColor,
                topLeft = Offset(slot * index + (slot - width) / 2f, size.height - height),
                size = Size(width, height), cornerRadius = CornerRadius(width / 2, width / 2))
        }
    }
}
