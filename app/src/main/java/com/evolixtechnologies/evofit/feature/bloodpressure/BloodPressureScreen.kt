package com.evolixtechnologies.evofit.feature.bloodpressure

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonitorHeart
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.evolixtechnologies.evofit.core.designsystem.EvoBlue
import com.evolixtechnologies.evofit.core.designsystem.EvoCard
import com.evolixtechnologies.evofit.core.designsystem.EvoMuted
import com.evolixtechnologies.evofit.core.designsystem.EvoPrimaryButton
import com.evolixtechnologies.evofit.core.designsystem.EvoGreen
import com.evolixtechnologies.evofit.core.designsystem.EvoTopBar

@Composable
fun BloodPressureScreen(onSave: (Int, Int, Int?) -> Unit) {
    var sys by remember { mutableStateOf("") }
    var dia by remember { mutableStateOf("") }
    var pulse by remember { mutableStateOf("") }
    val valid = (sys.toIntOrNull() ?: 0) in 70..250 && (dia.toIntOrNull() ?: 0) in 40..150
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        EvoTopBar("Blood Pressure", subtitle = "Record a reading from a validated cuff.")
        Surface(shape = RoundedCornerShape(16.dp), color = EvoGreen.copy(alpha = .07f), tonalElevation = 1.dp) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Box(Modifier.size(54.dp).background(EvoGreen.copy(alpha = .14f), RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Info, null, tint = Color(0xFF087816))
                }
                Text("A phone camera cannot directly measure clinical blood pressure reliably.", style = MaterialTheme.typography.bodyLarge, color = Color(0xFF063F14))
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ReadingField("SYS", sys, Modifier.weight(1f)) { sys = it }
            ReadingField("DIA", dia, Modifier.weight(1f)) { dia = it }
        }
        ReadingField("Pulse (optional)", pulse, Modifier.fillMaxWidth()) { pulse = it }
        Spacer(Modifier.weight(1f))
        EvoPrimaryButton("Save reading", { onSave(sys.toIntOrNull() ?: return@EvoPrimaryButton, dia.toIntOrNull() ?: return@EvoPrimaryButton, pulse.toIntOrNull()) }, enabled = valid)
    }
}

@Composable
private fun ReadingField(label: String, value: String, modifier: Modifier, onChange: (String) -> Unit) {
    EvoCard(modifier = modifier) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(label, style = MaterialTheme.typography.titleSmall)
            Text(if (label == "SYS") "Systolic (mmHg)" else if (label == "DIA") "Diastolic (mmHg)" else "Heart rate (BPM)", style = MaterialTheme.typography.bodyMedium, color = EvoMuted)
            OutlinedTextField(
                value = value,
                onValueChange = { onChange(it.filter(Char::isDigit).take(3)) },
                label = null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = { Text(if (label == "Pulse (optional)") "BPM" else "mmHg", color = EvoMuted, modifier = Modifier.padding(end = 8.dp)) },
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}
