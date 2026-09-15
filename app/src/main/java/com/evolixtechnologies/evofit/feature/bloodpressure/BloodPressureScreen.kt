package com.evolixtechnologies.evofit.feature.bloodpressure

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.evolixtechnologies.evofit.core.designsystem.EvoGreen

@Composable
fun BloodPressureScreen(onSave: (Int, Int, Int?) -> Unit) {
    var sys by remember { mutableStateOf("") }
    var dia by remember { mutableStateOf("") }
    var pulse by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Blood Pressure", style = MaterialTheme.typography.titleLarge)
        Text("Enter a reading from a validated cuff. A standard phone camera cannot directly measure clinical blood pressure reliably.", style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(sys, { sys = it.filter(Char::isDigit).take(3) }, label = { Text("Systolic (SYS)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(dia, { dia = it.filter(Char::isDigit).take(3) }, label = { Text("Diastolic (DIA)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(pulse, { pulse = it.filter(Char::isDigit).take(3) }, label = { Text("Pulse (optional)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.weight(1f))
        Button(
            onClick = { onSave(sys.toIntOrNull() ?: return@Button, dia.toIntOrNull() ?: return@Button, pulse.toIntOrNull()) },
            enabled = (sys.toIntOrNull() ?: 0) in 70..250 && (dia.toIntOrNull() ?: 0) in 40..150,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = EvoGreen, contentColor = Color.Black)
        ) { Text("Save reading") }
    }
}
