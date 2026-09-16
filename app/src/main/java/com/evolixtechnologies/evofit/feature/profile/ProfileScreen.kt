package com.evolixtechnologies.evofit.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.evolixtechnologies.evofit.core.designsystem.EvoBlue
import com.evolixtechnologies.evofit.core.designsystem.EvoCard
import com.evolixtechnologies.evofit.core.designsystem.EvoCardShape
import com.evolixtechnologies.evofit.core.designsystem.EvoGreen
import com.evolixtechnologies.evofit.core.designsystem.EvoListRow
import com.evolixtechnologies.evofit.core.designsystem.EvoMuted
import com.evolixtechnologies.evofit.core.designsystem.EvoPurple
import com.evolixtechnologies.evofit.core.designsystem.EvoRed
import com.evolixtechnologies.evofit.core.designsystem.EvoTopBar

@Composable
fun ProfileScreen(
    name: String,
    age: String,
    height: String,
    weight: String,
    gender: String,
    dailyGoalSummary: String,
    onHealthProfile: () -> Unit,
    onDailyGoals: () -> Unit,
    onNotifications: () -> Unit,
    onAppearance: () -> Unit,
    onDataPrivacy: () -> Unit,
    onHelp: () -> Unit,
    onAbout: () -> Unit
) {
    val displayName = name.ifBlank { "EvoFit User" }
    val initials = displayName
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifBlank { "EU" }

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text("Profile", style = MaterialTheme.typography.titleLarge)
            Text("Manage your account and preferences.", style = MaterialTheme.typography.bodyLarge, color = EvoMuted)
        }
        EvoCard {
            Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(Modifier.size(86.dp).background(EvoGreen.copy(alpha = .16f), CircleShape), contentAlignment = Alignment.Center) {
                    Text(initials, color = Color(0xFF087816), fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleLarge)
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(displayName, style = MaterialTheme.typography.titleMedium)
                    Text("$gender - $age yrs", style = MaterialTheme.typography.bodyMedium, color = EvoMuted)
                    Box(Modifier.background(EvoGreen.copy(alpha = .13f), RoundedCornerShape(24.dp)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                        Text("Healthier you starts today.", style = MaterialTheme.typography.bodySmall, color = Color(0xFF0B6E14))
                    }
                }
            }
        }
        Text("Settings", style = MaterialTheme.typography.titleSmall)
        SettingsGroup {
            EvoListRow(Icons.Default.Person, "Health profile", "$age yrs - $height cm - $weight kg", onClick = onHealthProfile)
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = .6f))
            EvoListRow(Icons.Default.Flag, "Daily goals", dailyGoalSummary, onClick = onDailyGoals)
        }
        SettingsGroup {
            EvoListRow(Icons.Default.HealthAndSafety, "Health Connect", "Sync with other health apps")
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = .6f))
            EvoListRow(Icons.Default.Notifications, "Notifications", "Manage your alerts", tint = EvoRed, onClick = onNotifications)
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = .6f))
            EvoListRow(Icons.Default.Palette, "Appearance", "Theme and display settings", tint = EvoPurple, onClick = onAppearance)
        }
        SettingsGroup {
            EvoListRow(Icons.Default.PrivacyTip, "Data & Privacy", "Manage your data", onClick = onDataPrivacy)
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = .6f))
            EvoListRow(Icons.Default.Help, "Help & Support", "Get help or contact us", tint = EvoBlue, onClick = onHelp)
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = .6f))
            EvoListRow(Icons.Default.Info, "About", "App version and legal info", tint = Color(0xFF737A8C), onClick = onAbout)
        }
        Text("EvoFit - Step Tracker - v1.0.0", style = MaterialTheme.typography.bodySmall, color = EvoMuted, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
fun NotificationSettingsScreen(enabled: Boolean, onEnabledChange: (Boolean) -> Unit, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        EvoTopBar("Notifications", subtitle = "Manage the ongoing step tracking notification.", onBack = onBack)
        SettingsPanel {
            Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Step tracking service", style = MaterialTheme.typography.titleSmall)
                    Text(
                        if (enabled) "Background step counting is active and shown with a persistent notification." else "Background step counting is stopped. Home tracking can be started again anytime.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = EvoMuted
                    )
                }
                Switch(
                    checked = enabled,
                    onCheckedChange = onEnabledChange,
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = EvoGreen)
                )
            }
        }
    }
}

@Composable
fun DataPrivacyScreen(onBack: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        EvoTopBar("Data & Privacy", subtitle = "Privacy policy for EvoFit - Step Tracker.", onBack = onBack)
        PrivacySection("Data we use", "EvoFit stores profile details, daily goals, step totals, activity logs, sleep logs, heart rate readings, and blood pressure records locally on your device.")
        PrivacySection("Permissions", "Activity Recognition is used for step counting. Camera permission is used only during heart-rate measurement. Notification permission is used for the foreground tracking notification.")
        PrivacySection("Background tracking", "When you start tracking, EvoFit may run a foreground service so step counting can continue while the app is in the background.")
        PrivacySection("Data sharing", "This app does not sell your personal data. Current app data is stored on device unless you choose to share it through your phone or operating-system services.")
        PrivacySection("Wellness notice", "Heart rate and blood pressure features are wellness tools only and are not a medical diagnosis, treatment, or replacement for professional medical advice.")
        PrivacySection("Contact", "For privacy or support questions, contact info@evolixtechnologies.com.")
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
fun AppearanceScreen(themeMode: String, onThemeSelected: (String) -> Unit, onBack: () -> Unit) {
    var theme by remember(themeMode) { mutableStateOf(themeMode) }
    val pageColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground
    val mutedColor = MaterialTheme.colorScheme.onBackground.copy(alpha = .62f)

    Column(Modifier.fillMaxSize().background(pageColor).padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(Modifier.size(58.dp).background(EvoGreen.copy(alpha = .12f), CircleShape), contentAlignment = Alignment.Center) {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back", tint = Color(0xFF087816)) }
            }
            Column {
                Text("Appearance", color = textColor, style = MaterialTheme.typography.titleLarge)
                Text("Choose how EvoFit looks on your device.", color = mutedColor, style = MaterialTheme.typography.bodyLarge)
            }
        }
        Text("THEME", color = mutedColor, style = MaterialTheme.typography.labelLarge)
        SettingsPanel {
            Column(Modifier.padding(14.dp)) {
                ThemeOption("System", "Follow your device's system theme.", Icons.Default.PhoneAndroid, theme == "System") {
                    theme = "System"
                    onThemeSelected("System")
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = .6f))
                ThemeOption("Light", "Always use light theme.", Icons.Default.LightMode, theme == "Light") {
                    theme = "Light"
                    onThemeSelected("Light")
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = .6f))
                ThemeOption("Dark", "Always use dark theme.", Icons.Default.DarkMode, theme == "Dark") {
                    theme = "Dark"
                    onThemeSelected("Dark")
                }
            }
        }
    }
}

@Composable
private fun ThemeOption(title: String, subtitle: String, icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(if (selected) EvoGreen.copy(alpha = .10f) else Color.Transparent, RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(Modifier.size(62.dp).background(EvoGreen.copy(alpha = .11f), RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = if (selected) Color(0xFF087816) else MaterialTheme.colorScheme.onSurface)
        }
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = EvoMuted)
        }
        RadioButton(selected = selected, onClick = onClick, colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF087816)))
    }
}

@Composable
private fun SettingsGroup(content: @Composable () -> Unit) {
    EvoCard {
        Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            content()
        }
    }
}

@Composable
private fun PrivacySection(title: String, body: String) {
    SettingsPanel {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(body, style = MaterialTheme.typography.bodyMedium, color = EvoMuted)
        }
    }
}

@Composable
private fun SettingsPanel(content: @Composable () -> Unit) {
    Surface(shape = EvoCardShape, color = MaterialTheme.colorScheme.surface, contentColor = MaterialTheme.colorScheme.onSurface, tonalElevation = 0.dp, shadowElevation = 1.dp) {
        Box(Modifier.fillMaxWidth()) { content() }
    }
}
