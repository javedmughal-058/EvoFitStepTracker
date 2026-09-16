package com.evolixtechnologies.evofit.feature.sleep

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import com.evolixtechnologies.evofit.core.designsystem.EvoBlue
import com.evolixtechnologies.evofit.core.designsystem.EvoFilterChip
import com.evolixtechnologies.evofit.core.designsystem.EvoGreen
import com.evolixtechnologies.evofit.core.designsystem.EvoMuted
import com.evolixtechnologies.evofit.core.designsystem.EvoPrimaryButton
import com.evolixtechnologies.evofit.core.designsystem.EvoPurple
import com.evolixtechnologies.evofit.core.designsystem.EvoTopBar
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Composable
fun SleepScreen(onSave: (String, String, Int, String, String) -> Unit) {
    var quality by remember { mutableStateOf("Good") }
    var bedtime by remember { mutableStateOf("22:00") }
    var wakeTime by remember { mutableStateOf("06:00") }
    var notes by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        EvoTopBar("Sleep")
        Text("Log sleep", style = MaterialTheme.typography.titleSmall)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                bedtime,
                { bedtime = formatTimeInput(it) },
                label = { Text("Bedtime") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                wakeTime,
                { wakeTime = formatTimeInput(it) },
                label = { Text("Wake up") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
        error?.let { Text(it, color = Color(0xFFFF4E64), style = MaterialTheme.typography.bodySmall) }
        SleepStages(Modifier.fillMaxWidth().height(28.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("Deep", "Light", "REM", "Awake").forEachIndexed { index, label ->
                Text(label, color = listOf(EvoBlue, Color(0xFF6377FF), EvoPurple, Color(0xFFD0D4DD))[index], style = MaterialTheme.typography.bodySmall)
            }
        }
        Text("Sleep quality", style = MaterialTheme.typography.titleSmall)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Poor", "Fair", "Good", "Great").forEach { q ->
                EvoFilterChip(
                    selected = quality == q,
                    text = q,
                    onClick = { quality = q },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        OutlinedTextField(notes, { notes = it.take(160) }, label = { Text("Notes (optional)") }, modifier = Modifier.fillMaxWidth().height(72.dp))
        Spacer(Modifier.weight(1f))
        EvoPrimaryButton("Save", {
            val duration = sleepDurationMinutes(bedtime, wakeTime)
            if (duration == null) {
                error = "Enter time as HH:mm, for example 22:00 and 06:00."
            } else {
                error = null
                onSave(bedtime, wakeTime, duration, quality, notes.trim())
            }
        })
    }
}

@Composable
private fun MetricRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = EvoMuted)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SleepStages(modifier: Modifier) {
    Canvas(modifier) {
        val colors = listOf(EvoBlue, Color(0xFF6377FF), EvoPurple, Color(0xFFD0D4DD), Color(0xFF6377FF))
        val widths = listOf(.22f, .34f, .18f, .08f, .18f)
        var x = 0f
        widths.forEachIndexed { index, fraction ->
            val w = size.width * fraction
            drawRoundRect(colors[index], Offset(x, 0f), Size(w - 3f, size.height), CornerRadius(9f, 9f))
            x += w
        }
    }
}

private fun formatTimeInput(value: String): String {
    val digits = value.filter(Char::isDigit).take(4)
    return if (digits.length <= 2) digits else digits.take(2) + ":" + digits.drop(2)
}

private fun sleepDurationMinutes(bedtime: String, wakeTime: String): Int? {
    return try {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val bed = LocalTime.parse(bedtime, formatter)
        val wake = LocalTime.parse(wakeTime, formatter)
        val today = LocalDate.now()
        val bedAt = LocalDateTime.of(today, bed)
        val wakeAt = LocalDateTime.of(if (wake.isAfter(bed)) today else today.plusDays(1), wake)
        Duration.between(bedAt, wakeAt).toMinutes().toInt().takeIf { it in 1..1440 }
    } catch (_: DateTimeParseException) {
        null
    }
}
