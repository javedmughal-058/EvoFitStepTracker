package com.evolixtechnologies.evofit.feature.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(onAbout: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("Profile", style = MaterialTheme.typography.titleLarge)
        Text("EvoFit user", style = MaterialTheme.typography.titleSmall)
        Text("by Evolix Technologies", style = MaterialTheme.typography.bodySmall)
        HorizontalDivider(Modifier.padding(vertical = 8.dp))
        ProfileRow(Icons.Default.Person, "Health profile")
        ProfileRow(Icons.Default.Flag, "Daily goals")
        ProfileRow(Icons.Default.HealthAndSafety, "Health Connect")
        ProfileRow(Icons.Default.Notifications, "Notifications")
        ProfileRow(Icons.Default.Palette, "Appearance")
        ProfileRow(Icons.Default.PrivacyTip, "Data & Privacy")
        ProfileRow(Icons.Default.Help, "Help & Support")
        ProfileRow(Icons.Default.Info, "About", onAbout)
        Spacer(Modifier.weight(1f))
        Text("EvoFit - Step Tracker • v1.0.0", style = MaterialTheme.typography.bodySmall)
        Text("info@evolixtechnologies.com", style = MaterialTheme.typography.bodySmall)
        Text("evolixtechnologies.com", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable private fun ProfileRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, onClick: () -> Unit = {}) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = { Text(title, style = MaterialTheme.typography.bodyLarge) },
        leadingContent = { Icon(icon, null) },
        trailingContent = { Text("›") }
    )
}
