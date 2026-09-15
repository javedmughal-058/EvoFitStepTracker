package com.evolixtechnologies.evofit.feature.measure

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.evolixtechnologies.evofit.core.designsystem.EvoBlue
import com.evolixtechnologies.evofit.core.designsystem.EvoPurple
import com.evolixtechnologies.evofit.core.designsystem.EvoRed

@Composable
fun MeasureScreen(onHeart: () -> Unit, onBp: () -> Unit, onSleep: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Measure", style = MaterialTheme.typography.titleLarge)
        Text("Check your latest wellness metrics.", style = MaterialTheme.typography.bodyMedium)
        ActionCard("Heart Rate", "Measure using your phone camera.", Icons.Default.Favorite, EvoRed, onHeart)
        ActionCard("Blood Pressure", "Record a cuff reading.", Icons.Default.MonitorHeart, EvoBlue, onBp)
        ActionCard("Log Sleep", "Track your sleep time.", Icons.Default.Bedtime, EvoPurple, onSleep)
        Text("Camera blood-pressure estimation is intentionally not presented as a medical measurement.", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable private fun ActionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    onClick: () -> Unit
) {
    Card(onClick = onClick, shape = RoundedCornerShape(14.dp)) {
        Row(Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(icon, null, tint = tint)
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall)
                Text(subtitle, style = MaterialTheme.typography.bodySmall)
            }
            Text("›")
        }
    }
}
