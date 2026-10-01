package com.evolixtechnologies.evofit.feature.history

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import com.evolixtechnologies.evofit.core.designsystem.EvoBlue
import com.evolixtechnologies.evofit.core.designsystem.EvoCard
import com.evolixtechnologies.evofit.core.designsystem.EvoGreen
import com.evolixtechnologies.evofit.core.designsystem.EvoMuted
import com.evolixtechnologies.evofit.core.designsystem.EvoRed
import com.evolixtechnologies.evofit.core.designsystem.EvoTopBar
import com.evolixtechnologies.evofit.core.designsystem.IconPill
import com.evolixtechnologies.evofit.data.local.ActivityLogEntity
import com.evolixtechnologies.evofit.data.local.BloodPressureEntity
import com.evolixtechnologies.evofit.data.local.HeartRateEntity
import com.evolixtechnologies.evofit.data.local.SleepLogEntity
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun HistoryScreen(
    heartRates: List<HeartRateEntity>,
    bloodPressure: List<BloodPressureEntity>,
    activities: List<ActivityLogEntity>,
    sleepLogs: List<SleepLogEntity>,
    stepsToday: Int,
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = context.getSharedPreferences("evofit_steps", Context.MODE_PRIVATE)
    val today = LocalDate.now()
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val records = buildList {
        heartRates.forEach { add(HistoryRecord("Heart rate", "${it.bpm} BPM", it.context, it.timestamp, EvoRed)) }
        bloodPressure.forEach { add(HistoryRecord("Blood pressure", "${it.systolic} / ${it.diastolic}", it.pulse?.let { pulse -> "Pulse $pulse" } ?: "Saved reading", it.timestamp, EvoBlue)) }
        activities.forEach { add(HistoryRecord(it.title, "${it.minutes} min", "${it.calories} kcal", it.timestamp, EvoGreen)) }
        sleepLogs.forEach { add(HistoryRecord("Sleep", "${it.durationMinutes / 60}h ${it.durationMinutes % 60}m", it.quality, it.timestamp, EvoBlue)) }
        val dates = prefs.all.keys.mapNotNull { key ->
            if (!key.startsWith("steps_")) null
            else runCatching { LocalDate.parse(key.removePrefix("steps_")) }.getOrNull()
        }.filter { !it.isAfter(today) }.toSet()
        dates.forEach { date ->
            val steps = if (date == today) stepsToday else prefs.getInt("steps_$date", 0)
            add(HistoryRecord(
                "Daily activity",
                "%,d steps".format(steps),
                "%.2f km  |  %.0f kcal  |  %d min".format(steps * 0.00078, steps * 0.0445, steps / 119),
                date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                EvoGreen
            ))
        }
    }.sortedByDescending { it.timestamp }
    val shownRecords = if (selectedDate == null) records else records.filter {
        Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDate() == selectedDate
    }

    LazyColumn(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { EvoTopBar("History", subtitle = "Your activity and saved measurements.", onBack = onBack) }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    Surface(onClick = { selectedDate = null }, shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                        color = if (selectedDate == null) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface) {
                        Text("All", Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = if (selectedDate == null) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface)
                    }
                }
                items((0..13).map { today.minusDays(it.toLong()) }) { date ->
                    Surface(onClick = { selectedDate = date }, shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                        color = if (selectedDate == date) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface) {
                        Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(date.format(DateTimeFormatter.ofPattern("EEE")),
                                style = MaterialTheme.typography.bodySmall, color = EvoMuted, textAlign = TextAlign.Center)
                            Text(date.dayOfMonth.toString(), style = MaterialTheme.typography.titleSmall,
                                color = if (selectedDate == date) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface)
                            Spacer(Modifier.size(5.dp).background(
                                if (records.any { Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDate() == date })
                                    MaterialTheme.colorScheme.primary else Color.Transparent, CircleShape))
                        }
                    }
                }
            }
        }
        if (shownRecords.isEmpty()) {
            item { EvoCard {
                Text(
                    if (selectedDate == null) "No records yet. Your activity and saved measurements will appear here."
                    else "No records for this day.",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = EvoMuted
                )
            } }
        } else {
            items(shownRecords) { record ->
                HistoryRow(record)
            }
        }
    }
}

@Composable
private fun HistoryRow(record: HistoryRecord) {
    EvoCard {
        Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            IconPill(
                when (record.title) {
                    "Heart rate" -> Icons.Default.Favorite
                    "Blood pressure" -> Icons.Default.MonitorHeart
                    else -> Icons.Default.DirectionsRun
                },
                record.tint,
                Modifier.size(68.dp)
            )
            Column(Modifier.weight(1f)) {
                Text(record.title, style = MaterialTheme.typography.titleSmall)
                Text(record.subtitle, style = MaterialTheme.typography.bodyLarge, color = EvoMuted)
            }
            Column(horizontalAlignment = Alignment.End, modifier = Modifier.background(record.tint.copy(alpha = .10f), androidx.compose.foundation.shape.RoundedCornerShape(16.dp)).padding(horizontal = 14.dp, vertical = 10.dp)) {
                Text(record.value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Text(formatTime(record.timestamp), style = MaterialTheme.typography.bodySmall, color = EvoMuted)
            }
        }
    }
}

private fun formatTime(timestamp: Long): String {
    return Instant.ofEpochMilli(timestamp)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("d MMM, h:mm a"))
}

private data class HistoryRecord(
    val title: String,
    val value: String,
    val subtitle: String,
    val timestamp: Long,
    val tint: Color
)
