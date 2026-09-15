package com.evolixtechnologies.evofit.feature.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.evolixtechnologies.evofit.R

@Composable
fun AboutScreen() {
    Column(Modifier.fillMaxSize().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("About", style = MaterialTheme.typography.titleLarge, modifier = Modifier.align(Alignment.Start))
        Spacer(Modifier.height(24.dp))
        Image(painterResource(R.drawable.evolix_logo), contentDescription = "Evolix Technologies", modifier = Modifier.fillMaxWidth(.58f).height(110.dp))
        Text("EvoFit - Step Tracker", style = MaterialTheme.typography.titleMedium)
        Text("Version 1.0.0", style = MaterialTheme.typography.bodySmall)
        Card(shape = RoundedCornerShape(14.dp)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Developed by Evolix Technologies", style = MaterialTheme.typography.bodyLarge)
                Text("info@evolixtechnologies.com", style = MaterialTheme.typography.bodyMedium)
                Text("evolixtechnologies.com", style = MaterialTheme.typography.bodyMedium)
            }
        }
        Spacer(Modifier.weight(1f))
        Text("© 2026 Evolix Technologies", style = MaterialTheme.typography.bodySmall)
    }
}
