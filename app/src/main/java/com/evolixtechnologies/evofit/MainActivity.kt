package com.evolixtechnologies.evofit

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startForegroundService
import androidx.navigation.compose.rememberNavController
import com.evolixtechnologies.evofit.core.designsystem.EvoFitTheme
import com.evolixtechnologies.evofit.core.navigation.EvoFitNavHost
import com.evolixtechnologies.evofit.core.sensors.StepCounterManager
import com.evolixtechnologies.evofit.core.sensors.StepTrackingService
import com.evolixtechnologies.evofit.data.local.AppDatabase
import com.evolixtechnologies.evofit.data.repository.HealthRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val stepCounter = StepCounterManager(this)
        val repository = HealthRepository(AppDatabase.get(this).healthDao())
        val prefs = getSharedPreferences("evofit_app", MODE_PRIVATE)

        fun hasActivityPermission(): Boolean =
            Build.VERSION.SDK_INT < 29 ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED

        fun hasNotificationPermission(): Boolean =
            Build.VERSION.SDK_INT < 33 ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

        fun startTrackingService() {
            val intent = Intent(this, StepTrackingService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(this, intent)
            } else {
                startService(intent)
            }
        }

        setContent {
            val nav = rememberNavController()
            var themeMode by remember { mutableStateOf(prefs.getString("theme_mode", "System") ?: "System") }
            val systemDark = isSystemInDarkTheme()
            val useDarkTheme = when (themeMode) {
                "Light" -> false
                "Dark" -> true
                else -> systemDark
            }

            EvoFitTheme(darkTheme = useDarkTheme) {
                val steps by stepCounter.steps.collectAsState()
                val paused by stepCounter.paused.collectAsState()
                var onboardingDone by remember { mutableStateOf(prefs.getBoolean("onboarding_done", false)) }
                var userName by remember { mutableStateOf(prefs.getString("user_name", "") ?: "") }
                var age by remember { mutableStateOf(prefs.getString("age", "26") ?: "26") }
                var height by remember { mutableStateOf(prefs.getString("height", "178") ?: "178") }
                var weight by remember { mutableStateOf(prefs.getString("weight", "74") ?: "74") }
                var gender by remember { mutableStateOf(prefs.getString("gender", "Male") ?: "Male") }
                var stepGoal by remember { mutableIntStateOf(prefs.getInt("step_goal", 8000)) }
                var distanceGoal by remember { mutableStateOf(prefs.getString("distance_goal", "4.0") ?: "4.0") }
                var activeGoal by remember { mutableStateOf(prefs.getString("active_goal", "45") ?: "45") }
                var caloriesGoal by remember { mutableStateOf(prefs.getString("calories_goal", "300") ?: "300") }
                var trackingEnabled by remember { mutableStateOf(prefs.getBoolean("tracking_enabled", false)) }
                var activityGranted by remember { mutableStateOf(hasActivityPermission()) }
                var notificationGranted by remember { mutableStateOf(hasNotificationPermission()) }
                var showTrackingDisclosure by remember { mutableStateOf(false) }
                val trackingPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
                    activityGranted = hasActivityPermission()
                    notificationGranted = hasNotificationPermission()
                    if (activityGranted) {
                        trackingEnabled = true
                        prefs.edit().putBoolean("tracking_enabled", true).apply()
                        stepCounter.start()
                        startTrackingService()
                    }
                }

                LaunchedEffect(onboardingDone, trackingEnabled, activityGranted) {
                    if (onboardingDone && trackingEnabled && activityGranted) {
                        stepCounter.start()
                        startTrackingService()
                    }
                }
                DisposableEffect(Unit) { onDispose { stepCounter.stop() } }

                if (showTrackingDisclosure) {
                    AlertDialog(
                        onDismissRequest = { showTrackingDisclosure = false },
                        title = { Text("Start step tracking") },
                        text = {
                            Text(
                                "EvoFit uses Activity Recognition to count your steps and an ongoing notification to keep tracking while the app is in the background. Some phones may also ask you to allow background activity or disable battery optimization for reliable counting."
                            )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showTrackingDisclosure = false
                                    val permissions = buildList {
                                        if (Build.VERSION.SDK_INT >= 29) add(Manifest.permission.ACTIVITY_RECOGNITION)
                                        if (Build.VERSION.SDK_INT >= 33) add(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                    if (permissions.isEmpty()) {
                                        activityGranted = true
                                        notificationGranted = true
                                        trackingEnabled = true
                                        prefs.edit().putBoolean("tracking_enabled", true).apply()
                                        stepCounter.start()
                                        startTrackingService()
                                    } else {
                                        trackingPermissionLauncher.launch(permissions.toTypedArray())
                                    }
                                }
                            ) { Text("Continue") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showTrackingDisclosure = false }) { Text("Not now") }
                        }
                    )
                }

                EvoFitNavHost(
                    nav = nav,
                    steps = steps,
                    stepsPaused = paused,
                    stepsSupported = stepCounter.isSupported(),
                    trackingEnabled = trackingEnabled,
                    activityPermissionGranted = activityGranted,
                    notificationPermissionGranted = notificationGranted,
                    repo = repository,
                    onboardingDone = onboardingDone,
                    userName = userName,
                    age = age,
                    height = height,
                    weight = weight,
                    gender = gender,
                    stepGoal = stepGoal,
                    distanceGoal = distanceGoal,
                    activeGoal = activeGoal,
                    caloriesGoal = caloriesGoal,
                    notificationServiceEnabled = trackingEnabled,
                    onSaveProfile = { profile ->
                        userName = profile.name
                        age = profile.age
                        height = profile.height
                        weight = profile.weight
                        gender = profile.gender
                        prefs.edit()
                            .putString("user_name", profile.name)
                            .putString("age", profile.age)
                            .putString("height", profile.height)
                            .putString("weight", profile.weight)
                            .putString("gender", profile.gender)
                            .apply()
                    },
                    onSaveGoal = { goals ->
                        stepGoal = goals.steps
                        distanceGoal = goals.distanceKm
                        activeGoal = goals.activeMinutes
                        caloriesGoal = goals.calories
                        prefs.edit()
                            .putInt("step_goal", goals.steps)
                            .putString("distance_goal", goals.distanceKm)
                            .putString("active_goal", goals.activeMinutes)
                            .putString("calories_goal", goals.calories)
                            .apply()
                    },
                    onPauseSteps = { stepCounter.pause() },
                    onResumeSteps = { stepCounter.resume() },
                    onResetToday = { stepCounter.resetToday() },
                    onStartTracking = { showTrackingDisclosure = true },
                    onStopTracking = {
                        trackingEnabled = false
                        prefs.edit().putBoolean("tracking_enabled", false).apply()
                        stepCounter.stop()
                        stopService(Intent(this, StepTrackingService::class.java))
                    },
                    themeMode = themeMode,
                    onSaveThemeMode = { mode ->
                        themeMode = mode
                        prefs.edit().putString("theme_mode", mode).apply()
                    },
                    onCompleteOnboarding = {
                        onboardingDone = true
                        prefs.edit().putBoolean("onboarding_done", true).apply()
                    }
                )
            }
        }
    }
}
