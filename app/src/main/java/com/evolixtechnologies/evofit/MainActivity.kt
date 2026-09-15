package com.evolixtechnologies.evofit

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.evolixtechnologies.evofit.core.designsystem.EvoFitTheme
import com.evolixtechnologies.evofit.core.navigation.EvoFitNavHost
import com.evolixtechnologies.evofit.core.sensors.StepCounterManager
import com.evolixtechnologies.evofit.data.local.AppDatabase
import com.evolixtechnologies.evofit.data.repository.HealthRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val stepCounter = StepCounterManager(this)
        val repository = HealthRepository(AppDatabase.get(this).healthDao())
        val prefs = getSharedPreferences("evofit_app", MODE_PRIVATE)

        setContent {
            EvoFitTheme {
                val nav = rememberNavController()
                val steps by stepCounter.steps.collectAsState()
                var onboardingDone by remember { mutableStateOf(prefs.getBoolean("onboarding_done", false)) }
                var activityGranted by remember {
                    mutableStateOf(
                        Build.VERSION.SDK_INT < 29 ||
                            ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED
                    )
                }
                val activityPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                    activityGranted = granted
                    if (granted) stepCounter.start()
                }

                LaunchedEffect(onboardingDone) {
                    if (onboardingDone) {
                        if (Build.VERSION.SDK_INT >= 29 && !activityGranted) activityPermission.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                        else stepCounter.start()
                    }
                }
                DisposableEffect(Unit) { onDispose { stepCounter.stop() } }

                EvoFitNavHost(
                    nav = nav,
                    steps = steps,
                    repo = repository,
                    onboardingDone = onboardingDone,
                    onCompleteOnboarding = {
                        onboardingDone = true
                        prefs.edit().putBoolean("onboarding_done", true).apply()
                    }
                )
            }
        }
    }
}
