package com.evolixtechnologies.evofit.feature.activity

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.evolixtechnologies.evofit.core.designsystem.EvoCard
import com.evolixtechnologies.evofit.core.designsystem.EvoGreen
import com.evolixtechnologies.evofit.core.designsystem.EvoMuted
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun ActivityScreen(steps: Int, goal: Int) {
    var tab by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    val today = LocalDate.now()
    val dayBars = remember(steps) { List(24) { if (it == LocalTime.now().hour) steps else 0 } }
    val weekBars = remember(steps) { dailySteps(context, today, 7, steps) }
    val monthBars = remember(steps) { dailySteps(context, today, 30, steps) }
    val bars = when (tab) {
        0 -> dayBars
        1 -> weekBars
        else -> monthBars
    }
    val total = bars.sum().takeIf { tab != 0 } ?: steps
    val safeGoal = goal.coerceAtLeast(1)
    val progress = (steps / safeGoal.toFloat()).coerceIn(0f, 1f)

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Activity", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleLarge)
            IconButton(onClick = {}) { Icon(Icons.Default.Search, contentDescription = "Search") }
        }
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
            listOf("Day", "Week", "Month").forEachIndexed { index, label ->
                SegmentedButton(
                    selected = tab == index,
                    onClick = { tab = index },
                    shape = SegmentedButtonDefaults.itemShape(index, 3),
                    label = { Text(label, fontSize = 12.sp) }
                )
            }
        }
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            FilledTonalIconButton(onClick = {}, modifier = Modifier.size(36.dp)) { Icon(Icons.Default.ChevronLeft, null) }
            Text(rangeLabel(tab, today), style = MaterialTheme.typography.titleSmall)
            FilledTonalIconButton(onClick = {}, modifier = Modifier.size(36.dp)) { Icon(Icons.Default.ChevronRight, null) }
        }
        EvoCard {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                    Icon(Icons.Default.DirectionsWalk, null, tint = EvoGreen, modifier = Modifier.size(58.dp))
                    Column(Modifier.weight(1f)) {
                        Text("%,d".format(total), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                        Text("Steps", style = MaterialTheme.typography.bodyMedium, color = EvoMuted)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Goal %,d".format(safeGoal), style = MaterialTheme.typography.bodySmall, color = EvoMuted)
                        Box(Modifier.fillMaxWidth(.28f).height(4.dp)) {
                            Canvas(Modifier.fillMaxSize()) {
                                drawRoundRect(Color(0xFFDDE2DA), cornerRadius = CornerRadius(8f, 8f))
                                drawRoundRect(EvoGreen, size = Size(size.width * progress, size.height), cornerRadius = CornerRadius(8f, 8f))
                            }
                        }
                    }
                }
                BarChart(modifier = Modifier.fillMaxWidth().height(170.dp), values = bars)
                HorizontalDivider()
                MetricRow("Distance", "%.1f km".format(total * 0.00078))
                MetricRow("Calories", "${(total * 0.0445).toInt()} kcal")
                MetricRow("Active time", "${total / 119} min")
            }
        }
        EvoCard {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Icon(Icons.Default.BarChart, null, tint = EvoGreen, modifier = Modifier.size(32.dp))
                Column {
                    Text(activityMessage(steps, safeGoal), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(if (steps == 0) "Start walking to build today's chart" else "Updated from your phone step sensor", style = MaterialTheme.typography.bodyMedium, color = EvoMuted)
                }
            }
        }
    }
}

private fun dailySteps(context: Context, today: LocalDate, days: Int, currentSteps: Int): List<Int> {
    val prefs = context.getSharedPreferences("evofit_steps", Context.MODE_PRIVATE)
    return (days - 1 downTo 0).map { offset ->
        val date = today.minusDays(offset.toLong()).toString()
        if (offset == 0) currentSteps else prefs.getInt("steps_$date", 0)
    }
}

private fun rangeLabel(tab: Int, today: LocalDate): String {
    return when (tab) {
        0 -> today.format(DateTimeFormatter.ofPattern("EEE, d MMM"))
        1 -> "${today.minusDays(6).format(DateTimeFormatter.ofPattern("d MMM"))} - ${today.format(DateTimeFormatter.ofPattern("d MMM"))}"
        else -> today.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
    }
}

private fun activityMessage(steps: Int, goal: Int): String {
    if (steps == 0) return "No steps yet"
    val percent = ((steps / goal.toFloat()) * 100).toInt()
    return "$percent% of daily goal"
}

@Composable
private fun MetricRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = EvoMuted)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun BarChart(modifier: Modifier, values: List<Int>) {
    Canvas(modifier) {
        val max = (values.maxOrNull() ?: 0).coerceAtLeast(1)
        val slot = size.width / values.size
        val barWidth = (slot * .42f).coerceAtLeast(4f)
        drawLine(Color(0xFFE8ECE5), Offset(0f, size.height), Offset(size.width, size.height), strokeWidth = 1f)
        values.forEachIndexed { i, v ->
            val h = if (v == 0) 2f else size.height * (v / max.toFloat()) * .82f
            drawRoundRect(
                color = if (v == 0) Color(0xFFDDE2DA) else EvoGreen,
                topLeft = Offset(slot * i + (slot - barWidth) / 2f, size.height - h),
                size = Size(barWidth, h),
                cornerRadius = CornerRadius(8f, 8f)
            )
        }
    }
}
