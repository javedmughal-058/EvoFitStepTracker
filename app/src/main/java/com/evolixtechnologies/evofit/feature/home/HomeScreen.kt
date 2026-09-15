package com.evolixtechnologies.evofit.feature.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.evolixtechnologies.evofit.core.designsystem.EvoGreen
import com.evolixtechnologies.evofit.core.designsystem.EvoRed

@Composable
fun HomeScreen(steps: Int, onMeasure: () -> Unit) {
    val goal = 8000
    val progress = (steps / goal.toFloat()).coerceIn(0f, 1f)
    val distance = steps * 0.00078
    val calories = (steps * 0.04).toInt()

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Good morning", style = MaterialTheme.typography.titleLarge)
        Text("Today", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = .55f))

        Card(shape = RoundedCornerShape(16.dp)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("TODAY'S STEPS", style = MaterialTheme.typography.labelSmall)
                    Text("${(progress * 100).toInt()}%", color = EvoGreen, style = MaterialTheme.typography.labelMedium)
                }
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.size(128.dp),
                        color = EvoGreen,
                        trackColor = EvoGreen.copy(alpha = .13f),
                        strokeWidth = 10.dp
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("%,d".format(steps), fontSize = 32.sp, fontWeight = FontWeight.Bold)
                        Text("of 8,000", style = MaterialTheme.typography.bodySmall)
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Metric("%.1f km".format(distance), "Distance")
                    Metric("$calories kcal", "Calories")
                    Metric("${(steps / 110).coerceAtLeast(0)} min", "Active")
                }
            }
        }

        Text("Health snapshot", style = MaterialTheme.typography.titleMedium)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SnapshotCard(Modifier.weight(1f), Icons.Default.Favorite, EvoRed, "Heart rate", "-- BPM", "Measure now")
            SnapshotCard(Modifier.weight(1f), Icons.Default.Hotel, Color(0xFF786BF2), "Sleep", "7h 26m", "Last night")
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SnapshotCard(Modifier.weight(1f), Icons.Default.Favorite, Color(0xFF4B8DFF), "Blood pressure", "-- / --", "Log reading")
            SnapshotCard(Modifier.weight(1f), Icons.Default.Scale, Color(0xFF9A6BFF), "Weight", "74.2 kg", "-0.4 kg")
        }

        Button(
            onClick = onMeasure,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EvoGreen, contentColor = Color.Black),
            shape = RoundedCornerShape(14.dp)
        ) { Text("Measure health", style = MaterialTheme.typography.labelLarge) }
    }
}

@Composable private fun Metric(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable private fun SnapshotCard(
    modifier: Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    title: String,
    value: String,
    subtitle: String
) {
    Card(modifier = modifier, shape = RoundedCornerShape(14.dp)) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(18.dp))
            Text(title, style = MaterialTheme.typography.bodySmall)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = .55f))
        }
    }
}
