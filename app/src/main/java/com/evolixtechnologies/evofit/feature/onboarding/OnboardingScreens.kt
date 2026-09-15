package com.evolixtechnologies.evofit.feature.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.evolixtechnologies.evofit.core.designsystem.EvoGreen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onDone: () -> Unit) {
    LaunchedEffect(Unit) { delay(900); onDone() }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("EvoFit", fontSize = 36.sp, fontWeight = FontWeight.Bold)
            Text("Step Tracker", style = MaterialTheme.typography.bodyMedium)
            Text("by Evolix Technologies", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun WelcomeScreen(onNext: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Spacer(Modifier.height(70.dp))
        Icon(Icons.Default.DirectionsWalk, null, tint = EvoGreen, modifier = Modifier.size(86.dp))
        Text("Small Steps\nBig Progress", style = MaterialTheme.typography.titleLarge)
        Text("Track your steps, stay active and build a healthier routine with EvoFit.", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.weight(1f))
        Button(onClick = onNext, modifier = Modifier.fillMaxWidth().height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = EvoGreen, contentColor = Color.Black)) { Text("Get Started") }
    }
}

@Composable
fun PersonalSetupScreen(onNext: () -> Unit) {
    var age by remember { mutableStateOf("26") }
    var height by remember { mutableStateOf("178") }
    var weight by remember { mutableStateOf("74") }
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Tell us about you", style = MaterialTheme.typography.titleLarge)
        Text("This helps personalize your activity and wellness insights.", style = MaterialTheme.typography.bodyMedium)
        SetupField("Age", age) { age = it }
        SetupField("Height (cm)", height) { height = it }
        SetupField("Weight (kg)", weight) { weight = it }
        Spacer(Modifier.weight(1f))
        Button(onClick = onNext, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = EvoGreen, contentColor = Color.Black)) { Text("Continue") }
    }
}

@Composable private fun SetupField(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = { onChange(it.filter(Char::isDigit).take(3)) },
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@Composable
fun GoalSetupScreen(onNext: () -> Unit) {
    var goal by remember { mutableIntStateOf(8000) }
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Your daily goal", style = MaterialTheme.typography.titleLarge)
        Text("Set a goal to stay motivated. You can change this anytime.", style = MaterialTheme.typography.bodyMedium)
        Card(shape = RoundedCornerShape(16.dp)) {
            Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                FilledTonalButton(onClick = { goal = (goal - 500).coerceAtLeast(1000) }) { Text("−") }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("%,d".format(goal), fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Text("Daily steps", style = MaterialTheme.typography.bodySmall)
                }
                FilledTonalButton(onClick = { goal = (goal + 500).coerceAtMost(30000) }) { Text("+") }
            }
        }
        Spacer(Modifier.weight(1f))
        Button(onClick = onNext, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = EvoGreen, contentColor = Color.Black)) { Text("Continue") }
    }
}

@Composable
fun HealthPermissionInfoScreen(onFinish: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Connect your health data", style = MaterialTheme.typography.titleLarge)
        Text("EvoFit can use Android activity and camera permissions for step and pulse features. Health Connect can be added as a production integration.", style = MaterialTheme.typography.bodyMedium)
        PermissionRow(Icons.Default.DirectionsWalk, "Steps")
        PermissionRow(Icons.Default.Hotel, "Sleep")
        PermissionRow(Icons.Default.Favorite, "Heart rate")
        Spacer(Modifier.weight(1f))
        Button(onClick = onFinish, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = EvoGreen, contentColor = Color.Black)) { Text("Continue to EvoFit") }
    }
}

@Composable private fun PermissionRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Icon(icon, null, tint = EvoGreen)
        Text(title, style = MaterialTheme.typography.bodyLarge)
    }
}
