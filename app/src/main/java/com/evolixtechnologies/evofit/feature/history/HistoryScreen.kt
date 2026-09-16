package com.evolixtechnologies.evofit.feature.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun HistoryScreen(
    heartRates: List<HeartRateEntity>,
    bloodPressure: List<BloodPressureEntity>,
    activities: List<ActivityLogEntity>,
    onBack: () -> Unit
) {
    val records = buildList {
        heartRates.forEach { add(HistoryRecord("Heart rate", "${it.bpm} BPM", it.context, it.timestamp, EvoRed)) }
        bloodPressure.forEach { add(HistoryRecord("Blood pressure", "${it.systolic} / ${it.diastolic}", it.pulse?.let { pulse -> "Pulse $pulse" } ?: "Saved reading", it.timestamp, EvoBlue)) }
        activities.forEach { add(HistoryRecord(it.title, "${it.minutes} min", "${it.calories} kcal", it.timestamp, EvoGreen)) }
    }.sortedByDescending { it.timestamp }

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        EvoTopBar("History", subtitle = "Your saved measurements.", onBack = onBack)
        if (records.isEmpty()) {
            EvoCard {
                Text(
                    "No records yet. Saved heart rate, blood pressure, and activity logs will appear here.",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = EvoMuted
                )
            }
        } else {
            records.forEach { record ->
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
