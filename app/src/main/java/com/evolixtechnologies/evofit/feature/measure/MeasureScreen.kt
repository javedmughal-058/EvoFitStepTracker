package com.evolixtechnologies.evofit.feature.measure

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.evolixtechnologies.evofit.core.designsystem.EvoBlue
import com.evolixtechnologies.evofit.core.designsystem.EvoCard
import com.evolixtechnologies.evofit.core.designsystem.EvoGreen
import com.evolixtechnologies.evofit.core.designsystem.EvoListRow
import com.evolixtechnologies.evofit.core.designsystem.EvoMuted
import com.evolixtechnologies.evofit.core.designsystem.EvoPurple
import com.evolixtechnologies.evofit.core.designsystem.EvoRed
import com.evolixtechnologies.evofit.core.designsystem.EvoTopBar
import com.evolixtechnologies.evofit.core.designsystem.IconPill

@Composable
fun MeasureScreen(
    onHeart: () -> Unit,
    onBp: () -> Unit,
    onSleep: () -> Unit,
    onActivity: () -> Unit,
    onHistory: () -> Unit
) {
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                BrandText()
                Text("Small Steps. Big Progress.", style = MaterialTheme.typography.bodyMedium, color = EvoMuted)
            }
            FilledIconButton(onClick = {}, modifier = Modifier.size(58.dp)) {
                Icon(Icons.Default.NotificationsNone, contentDescription = "Notifications")
            }
        }
        EvoTopBar("Measure", subtitle = "Check your wellness metrics.")
        ActionCard("Heart Rate", "Measure using your phone camera.", Icons.Default.Favorite, EvoRed, onHeart)
        ActionCard("Blood Pressure", "Record a cuff reading.", Icons.Default.MonitorHeart, EvoBlue, onBp)
        ActionCard("Log Sleep", "Track your sleep time.", Icons.Default.Bedtime, EvoBlue, onSleep)
        ActionCard("Log Activity", "Add your workout or activity.", Icons.Default.DirectionsRun, EvoGreen, onActivity)
        ActionCard("View History", "See all your measurements.", Icons.Default.History, Color(0xFFFF6B12), onHistory)
        Spacer(Modifier.weight(1f))
        Surface(shape = com.evolixtechnologies.evofit.core.designsystem.EvoCardShape, color = EvoGreen.copy(alpha = .08f)) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Box(Modifier.size(44.dp).background(EvoGreen.copy(alpha = .18f), CircleShape), contentAlignment = Alignment.Center) {
                    Text("i", color = Color(0xFF087816), style = MaterialTheme.typography.titleSmall)
                }
                Text(
                    "Heart-rate camera measurement is for wellness use only. Blood pressure should be entered from a validated cuff.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = EvoMuted
                )
            }
        }
    }
}

@Composable
fun LogActivityScreen(onSave: (String, Int, Int) -> Unit) {
    var title by remember { mutableStateOf("") }
    var minutes by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    val valid = title.trim().length >= 2 && (minutes.toIntOrNull() ?: 0) > 0

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        EvoTopBar("Log Activity", subtitle = "Add a workout or active session.")
        OutlinedTextField(
            value = title,
            onValueChange = { title = it.take(40) },
            label = { Text("Activity name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = minutes,
            onValueChange = { minutes = it.filter(Char::isDigit).take(3) },
            label = { Text("Duration (minutes)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )
        OutlinedTextField(
            value = calories,
            onValueChange = { calories = it.filter(Char::isDigit).take(4) },
            label = { Text("Calories (optional)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )
        Spacer(Modifier.weight(1f))
        com.evolixtechnologies.evofit.core.designsystem.EvoPrimaryButton(
            text = "Save activity",
            onClick = { onSave(title.trim(), minutes.toIntOrNull() ?: 0, calories.toIntOrNull() ?: 0) },
            enabled = valid
        )
    }
}

@Composable
private fun ActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit
) {
    EvoCard(onClick = onClick) {
        Row(
            Modifier.fillMaxWidth().padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            IconPill(icon, tint, Modifier.size(72.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall)
                Text(subtitle, style = MaterialTheme.typography.bodyLarge, color = EvoMuted)
            }
            Box(Modifier.size(48.dp).background(MaterialTheme.colorScheme.surfaceVariant, CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.ChevronRight, null, tint = Color(0xFF17203A))
            }
        }
    }
}

@Composable
private fun BrandText() {
    Row(verticalAlignment = Alignment.Bottom) {
        Text("Evo", style = MaterialTheme.typography.titleLarge)
        Text("Fit", style = MaterialTheme.typography.titleLarge, color = EvoGreen)
    }
}
