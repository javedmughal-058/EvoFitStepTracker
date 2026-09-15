package com.evolixtechnologies.evofit.feature.sleep

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.evolixtechnologies.evofit.core.designsystem.EvoGreen

@Composable
fun SleepScreen() {
    var quality by remember { mutableStateOf("Good") }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Sleep", style = MaterialTheme.typography.titleLarge)
        Text("Last night", style = MaterialTheme.typography.bodyMedium)
        Text("7h 34m", fontSize = 36.sp)
        Text("11:38 PM  →  7:12 AM", style = MaterialTheme.typography.bodyMedium)
        LinearProgressIndicator(progress = { .82f }, modifier = Modifier.fillMaxWidth(), color = Color(0xFF786BF2))
        Text("Sleep quality", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Poor", "Fair", "Good", "Great").forEach { q ->
                FilterChip(selected = quality == q, onClick = { quality = q }, label = { Text(q, fontSize = 10.sp) })
            }
        }
        Spacer(Modifier.weight(1f))
        Button(onClick = {}, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = EvoGreen, contentColor = Color.Black)) { Text("Save") }
    }
}
