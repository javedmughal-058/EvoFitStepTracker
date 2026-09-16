package com.evolixtechnologies.evofit.feature.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.evolixtechnologies.evofit.core.designsystem.EvoBlue
import com.evolixtechnologies.evofit.core.designsystem.EvoBlack
import com.evolixtechnologies.evofit.core.designsystem.EvoCard
import com.evolixtechnologies.evofit.core.designsystem.EvoGreen
import com.evolixtechnologies.evofit.core.designsystem.EvoMuted
import com.evolixtechnologies.evofit.core.designsystem.EvoPurple
import com.evolixtechnologies.evofit.core.designsystem.EvoRed
import com.evolixtechnologies.evofit.core.designsystem.EvoPrimaryButton
import com.evolixtechnologies.evofit.core.designsystem.EvoSoftGreen
import com.evolixtechnologies.evofit.core.designsystem.IconPill
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    steps: Int,
    goal: Int,
    paused: Boolean,
    stepsSupported: Boolean,
    trackingEnabled: Boolean,
    activityPermissionGranted: Boolean,
    notificationPermissionGranted: Boolean,
    latestHeartRate: String,
    latestHeartRateSubtitle: String,
    latestSleep: String,
    latestSleepSubtitle: String,
    latestBloodPressure: String,
    latestBloodPressureSubtitle: String,
    weight: String,
    onHeart: () -> Unit,
    onBp: () -> Unit,
    onSleep: () -> Unit,
    onAdd: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onResetToday: () -> Unit,
    onStartTracking: () -> Unit,
    onMeasure: () -> Unit
) {
    val shownSteps = steps.coerceAtLeast(0)
    val safeGoal = goal.coerceAtLeast(1)
    val progress = (shownSteps / safeGoal.toFloat()).coerceIn(0f, 1f)
    val distance = shownSteps * 0.00078
    val calories = (shownSteps * 0.0445).toInt()
    val active = shownSteps / 119
    val today = LocalDate.now().format(DateTimeFormatter.ofPattern("EEE, d MMM"))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp)
            .padding(bottom = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                BrandText()
                Text("Small Steps. Big Progress.", style = MaterialTheme.typography.bodyMedium, color = EvoMuted)
            }
            FilledIconButton(onClick = {}, modifier = Modifier.size(58.dp)) {
                Icon(Icons.Default.NotificationsNone, contentDescription = "Notifications")
            }
        }
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Good morning", style = MaterialTheme.typography.titleLarge)
                Text(today, style = MaterialTheme.typography.bodyLarge, color = EvoMuted)
            }
            Surface(shape = RoundedCornerShape(16.dp), color = EvoSoftGreen) {
                Text("A healthier you\nstarts today.", textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp), style = MaterialTheme.typography.bodyMedium, color = Color(0xFF0B6E14))
            }
        }

        EvoCard {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                    IconPill(Icons.Default.DirectionsRun, EvoGreen, Modifier.size(68.dp))
                    Column(Modifier.weight(1f).padding(start = 14.dp)) {
                        Text("Today's Steps", style = MaterialTheme.typography.bodyLarge, color = EvoMuted)
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("%,d".format(shownSteps), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                            Text(" / %,d".format(safeGoal), style = MaterialTheme.typography.titleSmall, color = EvoMuted, modifier = Modifier.padding(bottom = 2.dp))
                        }
                        Text("${(progress * 100).toInt()}% of daily goal", style = MaterialTheme.typography.bodyMedium, color = EvoMuted)
                    }
                    Surface(shape = RoundedCornerShape(16.dp), color = EvoSoftGreen) {
                        Column(Modifier.padding(horizontal = 14.dp, vertical = 9.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Goal", style = MaterialTheme.typography.bodySmall, color = EvoMuted)
                            Text("%,d".format(safeGoal), style = MaterialTheme.typography.titleSmall)
                        }
                    }
                }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.size(150.dp),
                            color = EvoGreen,
                            trackColor = Color(0xFFEAF0F2),
                            strokeWidth = 16.dp
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.DirectionsRun, null, tint = EvoGreen, modifier = Modifier.size(28.dp))
                            Text("${(progress * 100).toInt()}%", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                            Text("Goal reached", style = MaterialTheme.typography.bodySmall, color = EvoMuted)
                        }
                    }
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        MetricPill("%.1f km".format(distance), "Distance", EvoGreen)
                        MetricPill("$calories kcal", "Calories", Color(0xFFFF7A2E))
                        MetricPill("${active} min", "Active time", EvoBlue)
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilledTonalButton(
                        onClick = if (paused) onResume else onPause,
                        modifier = Modifier.weight(1f),
                        enabled = stepsSupported && trackingEnabled && activityPermissionGranted
                    ) {
                        Icon(if (paused) Icons.Default.PlayArrow else Icons.Default.Pause, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text(if (paused) "Resume" else "Pause", modifier = Modifier.padding(start = 6.dp))
                    }
                    FilledTonalButton(
                        onClick = onResetToday,
                        modifier = Modifier.weight(1f),
                        enabled = stepsSupported && trackingEnabled && activityPermissionGranted
                    ) {
                        Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text("Reset", modifier = Modifier.padding(start = 6.dp))
                    }
                }
                if (paused) {
                    Text("Step counting is paused. Resume when you want activity tracking to continue.", style = MaterialTheme.typography.bodySmall, color = EvoMuted)
                }
            }
        }

        if (!stepsSupported || !trackingEnabled || !activityPermissionGranted || !notificationPermissionGranted) {
            EvoCard {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Step tracking", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(
                        when {
                            !stepsSupported -> "This device does not report hardware step-counter data. EvoFit cannot count steps reliably on this phone."
                            !trackingEnabled -> "Start tracking to count steps while you use the app and keep counting in the background."
                            !activityPermissionGranted -> "Activity Recognition permission is required to count your steps."
                            else -> "Notification permission helps EvoFit keep background step tracking visible and reliable."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = EvoMuted
                    )
                    if (stepsSupported && (!trackingEnabled || !activityPermissionGranted || !notificationPermissionGranted)) {
                        EvoPrimaryButton("Start tracking", onStartTracking)
                    }
                }
            }
        }

        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Health snapshot", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
            Text("See all >", color = EvoGreen, style = MaterialTheme.typography.labelMedium)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SnapshotCard(Modifier.weight(1f), Icons.Default.Favorite, EvoRed, latestHeartRate, "Heart rate", latestHeartRateSubtitle)
            SnapshotCard(Modifier.weight(1f), Icons.Default.Bedtime, EvoBlue, latestSleep, "Sleep", latestSleepSubtitle)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SnapshotCard(Modifier.weight(1f), Icons.Default.MonitorHeart, EvoBlue, latestBloodPressure, "Blood pressure", latestBloodPressureSubtitle)
            SnapshotCard(Modifier.weight(1f), Icons.Default.Scale, EvoPurple, if (weight.isBlank()) "-- kg" else "$weight kg", "Weight", if (weight.isBlank()) "Add profile" else "Profile")
        }

            Text("Quick actions", style = MaterialTheme.typography.titleMedium)
        FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            QuickAction(Icons.Default.Favorite, "Heart rate", EvoRed, onHeart)
            QuickAction(Icons.Default.MonitorHeart, "Blood pressure", EvoBlue, onBp)
            QuickAction(Icons.Default.Bedtime, "Sleep", EvoBlue, onSleep)
            QuickAction(Icons.Default.Add, "Add", EvoPurple, onAdd)
        }
    }
}

@Composable
private fun SnapshotCard(
    modifier: Modifier,
    icon: ImageVector,
    tint: Color,
    value: String,
    title: String,
    subtitle: String
) {
    EvoCard(modifier = modifier) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
        ) {
            IconPill(icon, tint, Modifier.size(42.dp))
            Column(
                Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(title, style = MaterialTheme.typography.bodyMedium, color = EvoMuted)
                Text(value, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = EvoMuted, textAlign = TextAlign.Center)
            }
//            Box(Modifier.size(34.dp).background(MaterialTheme.colorScheme.surfaceVariant, CircleShape), contentAlignment = Alignment.Center) {
//                Text(">", color = EvoBlack, fontWeight = FontWeight.Bold)
//            }
        }
    }
}

@Composable
private fun QuickAction(icon: ImageVector, label: String, tint: Color, onClick: () -> Unit) {
    EvoCard(modifier = Modifier.size(width = 82.dp, height = 82.dp), onClick = onClick) {
        Column(
            Modifier.fillMaxSize().padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            IconPill(icon, tint, Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun BrandText() {
    Row(verticalAlignment = Alignment.Bottom) {
        Text("Evo", style = MaterialTheme.typography.titleLarge, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        Text("Fit", style = MaterialTheme.typography.titleLarge, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = EvoGreen)
    }
}

@Composable
private fun MetricPill(value: String, label: String, tint: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(Modifier.size(42.dp).background(tint.copy(alpha = .12f), CircleShape), contentAlignment = Alignment.Center) {
            Box(Modifier.size(10.dp).background(tint, CircleShape))
        }
        Column {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = EvoMuted)
            Text(value, style = MaterialTheme.typography.titleSmall)
        }
    }
}
