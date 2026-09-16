package com.evolixtechnologies.evofit.core.navigation

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.evolixtechnologies.evofit.core.designsystem.EvoGreen
import com.evolixtechnologies.evofit.data.repository.HealthRepository
import com.evolixtechnologies.evofit.feature.about.AboutScreen
import com.evolixtechnologies.evofit.feature.activity.ActivityScreen
import com.evolixtechnologies.evofit.feature.profile.AppearanceScreen
import com.evolixtechnologies.evofit.feature.profile.DataPrivacyScreen
import com.evolixtechnologies.evofit.feature.profile.NotificationSettingsScreen
import com.evolixtechnologies.evofit.feature.bloodpressure.BloodPressureScreen
import com.evolixtechnologies.evofit.feature.heartrate.*
import com.evolixtechnologies.evofit.feature.history.HistoryScreen
import com.evolixtechnologies.evofit.feature.home.HomeScreen
import com.evolixtechnologies.evofit.feature.measure.LogActivityScreen
import com.evolixtechnologies.evofit.feature.measure.MeasureScreen
import com.evolixtechnologies.evofit.feature.onboarding.*
import com.evolixtechnologies.evofit.feature.profile.ProfileScreen
import com.evolixtechnologies.evofit.feature.sleep.SleepScreen
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private const val WELCOME = "welcome"
private const val PERSONAL = "personal"
private const val GOAL = "goal"
private const val PERMISSIONS = "permissions"
private const val HOME = "home"
private const val ACTIVITY = "activity"
private const val MEASURE = "measure"
private const val PROFILE = "profile"
private const val HR_INTRO = "hr_intro"
private const val HR_MEASURE = "hr_measure"
private const val BP = "bp"
private const val SLEEP = "sleep"
private const val ABOUT = "about"
private const val APPEARANCE = "appearance"
private const val NOTIFICATIONS = "notifications"
private const val DATA_PRIVACY = "data_privacy"
private const val EDIT_PROFILE = "edit_profile"
private const val EDIT_GOAL = "edit_goal"
private const val LOG_ACTIVITY = "log_activity"
private const val HISTORY = "history"

@Composable
fun EvoFitNavHost(
    nav: NavHostController,
    steps: Int,
    stepsPaused: Boolean,
    stepsSupported: Boolean,
    trackingEnabled: Boolean,
    activityPermissionGranted: Boolean,
    notificationPermissionGranted: Boolean,
    repo: HealthRepository,
    onboardingDone: Boolean,
    userName: String,
    age: String,
    height: String,
    weight: String,
    gender: String,
    stepGoal: Int,
    distanceGoal: String,
    activeGoal: String,
    caloriesGoal: String,
    notificationServiceEnabled: Boolean,
    onSaveProfile: (ProfileSetup) -> Unit,
    onSaveGoal: (DailyGoals) -> Unit,
    onPauseSteps: () -> Unit,
    onResumeSteps: () -> Unit,
    onResetToday: () -> Unit,
    onStartTracking: () -> Unit,
    onStopTracking: () -> Unit,
    themeMode: String,
    onSaveThemeMode: (String) -> Unit,
    onCompleteOnboarding: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val currentEntry by nav.currentBackStackEntryAsState()
    val route = currentEntry?.destination?.route
    val mainRoutes = setOf(HOME, ACTIVITY, MEASURE, PROFILE)
    val navItemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = Color(0xFF087816),
        selectedTextColor = Color(0xFF087816),
        indicatorColor = EvoGreen.copy(alpha = .16f),
        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = .66f),
        unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = .66f)
    )

    Scaffold(
        bottomBar = {
            if (route in mainRoutes) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp)
                        .padding(top = 6.dp, bottom = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp,
                    shadowElevation = 1.dp
                ) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface, tonalElevation = 0.dp) {
                    NavigationBarItem(
                        selected = route == HOME,
                        onClick = { nav.navigate(HOME) { launchSingleTop = true; popUpTo(HOME) { saveState = true } } },
                        icon = { Icon(Icons.Default.Home, null) },
                        label = { Text("Home") },
                        colors = navItemColors
                    )
                    NavigationBarItem(
                        selected = route == ACTIVITY,
                        onClick = { nav.navigate(ACTIVITY) { launchSingleTop = true } },
                        icon = { Icon(Icons.Default.BarChart, null) },
                        label = { Text("Activity") },
                        colors = navItemColors
                    )
                    NavigationBarItem(
                        selected = route == MEASURE,
                        onClick = { nav.navigate(MEASURE) { launchSingleTop = true } },
                        icon = { Icon(Icons.Default.AddCircle, null) },
                        label = { Text("Measure") },
                        colors = navItemColors
                    )
                    NavigationBarItem(
                        selected = route == PROFILE,
                        onClick = { nav.navigate(PROFILE) { launchSingleTop = true } },
                        icon = { Icon(Icons.Default.Person, null) },
                        label = { Text("Profile") },
                        colors = navItemColors
                    )
                }
                }
            }
        }
    ) { padding ->
        NavHost(navController = nav, startDestination = if (onboardingDone) HOME else WELCOME, modifier = Modifier.padding(padding)) {
            composable(WELCOME) { WelcomeScreen { nav.navigate(PERSONAL) } }
            composable(PERSONAL) {
                PersonalSetupScreen(
                    initial = ProfileSetup(userName, age, height, weight, gender),
                    onNext = {
                        onSaveProfile(it)
                        nav.navigate(GOAL)
                    }
                )
            }
            composable(GOAL) {
                GoalSetupScreen(
                    initialGoals = DailyGoals(stepGoal, distanceGoal, activeGoal, caloriesGoal),
                    onNext = {
                        onSaveGoal(it)
                        nav.navigate(PERMISSIONS)
                    }
                )
            }
            composable(PERMISSIONS) { HealthPermissionInfoScreen { onCompleteOnboarding(); nav.navigate(HOME) { popUpTo(WELCOME) { inclusive = true } } } }
            composable(HOME) {
                val heartRates by repo.observeHeartRate().collectAsState(initial = emptyList())
                val bloodPressure by repo.observeBloodPressure().collectAsState(initial = emptyList())
                val sleepLogs by repo.observeSleep().collectAsState(initial = emptyList())
                val latestHeartRate = heartRates.firstOrNull()
                val latestBloodPressure = bloodPressure.firstOrNull()
                val latestSleep = sleepLogs.firstOrNull()
                HomeScreen(
                    steps = steps,
                    goal = stepGoal,
                    paused = stepsPaused,
                    stepsSupported = stepsSupported,
                    trackingEnabled = trackingEnabled,
                    activityPermissionGranted = activityPermissionGranted,
                    notificationPermissionGranted = notificationPermissionGranted,
                    latestHeartRate = latestHeartRate?.let { "${it.bpm} BPM" } ?: "-- BPM",
                    latestHeartRateSubtitle = latestHeartRate?.let { formatSnapshotTime(it.timestamp) } ?: "Measure now",
                    latestSleep = latestSleep?.let { formatSleepDuration(it.durationMinutes) } ?: "--",
                    latestSleepSubtitle = latestSleep?.let { it.quality } ?: "No log yet",
                    latestBloodPressure = latestBloodPressure?.let { "${it.systolic} / ${it.diastolic}" } ?: "-- / --",
                    latestBloodPressureSubtitle = latestBloodPressure?.let { formatSnapshotTime(it.timestamp) } ?: "No reading",
                    weight = weight,
                    onHeart = { nav.navigate(HR_INTRO) },
                    onBp = { nav.navigate(BP) },
                    onSleep = { nav.navigate(SLEEP) },
                    onAdd = { nav.navigate(LOG_ACTIVITY) },
                    onPause = onPauseSteps,
                    onResume = onResumeSteps,
                    onResetToday = onResetToday,
                    onStartTracking = onStartTracking,
                    onMeasure = { nav.navigate(MEASURE) }
                )
            }
            composable(ACTIVITY) { ActivityScreen(steps, stepGoal) }
            composable(MEASURE) {
                MeasureScreen(
                    onHeart = { nav.navigate(HR_INTRO) },
                    onBp = { nav.navigate(BP) },
                    onSleep = { nav.navigate(SLEEP) },
                    onActivity = { nav.navigate(LOG_ACTIVITY) },
                    onHistory = { nav.navigate(HISTORY) }
                )
            }
            composable(PROFILE) {
                ProfileScreen(
                    name = userName,
                    age = age,
                    height = height,
                    weight = weight,
                    gender = gender,
                    dailyGoalSummary = "$stepGoal steps - $distanceGoal km - $activeGoal min" + if (caloriesGoal.isBlank()) "" else " - $caloriesGoal kcal",
                    onHealthProfile = { nav.navigate(EDIT_PROFILE) },
                    onDailyGoals = { nav.navigate(EDIT_GOAL) },
                    onNotifications = { nav.navigate(NOTIFICATIONS) },
                    onAppearance = { nav.navigate(APPEARANCE) },
                    onDataPrivacy = { nav.navigate(DATA_PRIVACY) },
                    onHelp = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:info@evolixtechnologies.com")
                            putExtra(Intent.EXTRA_SUBJECT, "EvoFit support")
                        }
                        runCatching {
                            context.startActivity(Intent.createChooser(intent, "Contact Evolix support"))
                        }.onFailure {
                            Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onAbout = { nav.navigate(ABOUT) }
                )
            }
            composable(EDIT_PROFILE) {
                PersonalSetupScreen(
                    initial = ProfileSetup(userName, age, height, weight, gender),
                    onNext = {
                        onSaveProfile(it)
                        nav.popBackStack()
                    }
                )
            }
            composable(EDIT_GOAL) {
                GoalSetupScreen(
                    initialGoals = DailyGoals(stepGoal, distanceGoal, activeGoal, caloriesGoal),
                    onNext = {
                        onSaveGoal(it)
                        nav.popBackStack()
                    }
                )
            }
            composable(HR_INTRO) { HeartRateIntroScreen(onBack = { nav.popBackStack() }, onStart = { nav.navigate(HR_MEASURE) }) }
            composable(HR_MEASURE) { HeartRateMeasureScreen(onCancel = { nav.popBackStack() }) { bpm -> nav.navigate("hr_result/$bpm") { popUpTo(HR_MEASURE) { inclusive = true } } } }
            composable("hr_result/{bpm}") { entry ->
                val bpm = entry.arguments?.getString("bpm")?.toIntOrNull() ?: 0
                HeartRateResultScreen(
                    bpm = bpm,
                    onSave = { context -> scope.launch { repo.saveHeartRate(bpm, context) }; nav.navigate(MEASURE) { popUpTo(MEASURE) { inclusive = true } } },
                    onAgain = { nav.navigate(HR_MEASURE) }
                )
            }
            composable(BP) { BloodPressureScreen { sys, dia, pulse -> scope.launch { repo.saveBloodPressure(sys, dia, pulse) }; nav.popBackStack() } }
            composable(SLEEP) {
                SleepScreen { bedtime, wakeTime, durationMinutes, quality, notes ->
                    scope.launch { repo.saveSleep(bedtime, wakeTime, durationMinutes, quality, notes) }
                    nav.popBackStack()
                }
            }
            composable(LOG_ACTIVITY) { LogActivityScreen { title, minutes, calories -> scope.launch { repo.saveActivity(title, minutes, calories) }; nav.popBackStack() } }
            composable(HISTORY) {
                val heartRates by repo.observeHeartRate().collectAsState(initial = emptyList())
                val bloodPressure by repo.observeBloodPressure().collectAsState(initial = emptyList())
                val activities by repo.observeActivity().collectAsState(initial = emptyList())
                HistoryScreen(heartRates, bloodPressure, activities, onBack = { nav.popBackStack() })
            }
            composable(NOTIFICATIONS) {
                NotificationSettingsScreen(
                    enabled = notificationServiceEnabled,
                    onEnabledChange = { enabled ->
                        if (enabled) onStartTracking() else onStopTracking()
                    },
                    onBack = { nav.popBackStack() }
                )
            }
            composable(DATA_PRIVACY) { DataPrivacyScreen(onBack = { nav.popBackStack() }) }
            composable(APPEARANCE) { AppearanceScreen(themeMode = themeMode, onThemeSelected = onSaveThemeMode, onBack = { nav.popBackStack() }) }
            composable(ABOUT) { AboutScreen() }
        }
    }
}

private fun formatSnapshotTime(timestamp: Long): String {
    val formatter = DateTimeFormatter.ofPattern("h:mm a")
    return Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).format(formatter)
}

private fun formatSleepDuration(minutes: Int): String {
    val hours = minutes / 60
    val mins = minutes % 60
    return if (hours > 0) "${hours}h ${mins}m" else "${mins}m"
}
