package com.evolixtechnologies.evofit.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.evolixtechnologies.evofit.core.designsystem.EvoGreen
import com.evolixtechnologies.evofit.data.repository.HealthRepository
import com.evolixtechnologies.evofit.feature.about.AboutScreen
import com.evolixtechnologies.evofit.feature.activity.ActivityScreen
import com.evolixtechnologies.evofit.feature.bloodpressure.BloodPressureScreen
import com.evolixtechnologies.evofit.feature.heartrate.*
import com.evolixtechnologies.evofit.feature.home.HomeScreen
import com.evolixtechnologies.evofit.feature.measure.MeasureScreen
import com.evolixtechnologies.evofit.feature.onboarding.*
import com.evolixtechnologies.evofit.feature.profile.ProfileScreen
import com.evolixtechnologies.evofit.feature.sleep.SleepScreen
import kotlinx.coroutines.launch

private const val SPLASH = "splash"
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

@Composable
fun EvoFitNavHost(
    nav: NavHostController,
    steps: Int,
    repo: HealthRepository,
    onboardingDone: Boolean,
    onCompleteOnboarding: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val currentEntry by nav.currentBackStackEntryAsState()
    val route = currentEntry?.destination?.route
    val mainRoutes = setOf(HOME, ACTIVITY, MEASURE, PROFILE)

    Scaffold(
        bottomBar = {
            if (route in mainRoutes) {
                NavigationBar {
                    NavigationBarItem(
                        selected = route == HOME,
                        onClick = { nav.navigate(HOME) { launchSingleTop = true; popUpTo(HOME) { saveState = true } } },
                        icon = { Icon(Icons.Default.Home, null) },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = route == ACTIVITY,
                        onClick = { nav.navigate(ACTIVITY) { launchSingleTop = true } },
                        icon = { Icon(Icons.Default.BarChart, null) },
                        label = { Text("Activity") }
                    )
                    NavigationBarItem(
                        selected = route == MEASURE,
                        onClick = { nav.navigate(MEASURE) { launchSingleTop = true } },
                        icon = { Icon(Icons.Default.Add, null) },
                        label = { Text("Measure") },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = EvoGreen.copy(alpha = .25f),
                            selectedIconColor = Color.Black
                        )
                    )
                    NavigationBarItem(
                        selected = route == PROFILE,
                        onClick = { nav.navigate(PROFILE) { launchSingleTop = true } },
                        icon = { Icon(Icons.Default.Person, null) },
                        label = { Text("Profile") }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(navController = nav, startDestination = SPLASH, modifier = Modifier.padding(padding)) {
            composable(SPLASH) { SplashScreen { nav.navigate(if (onboardingDone) HOME else WELCOME) { popUpTo(SPLASH) { inclusive = true } } } }
            composable(WELCOME) { WelcomeScreen { nav.navigate(PERSONAL) } }
            composable(PERSONAL) { PersonalSetupScreen { nav.navigate(GOAL) } }
            composable(GOAL) { GoalSetupScreen { nav.navigate(PERMISSIONS) } }
            composable(PERMISSIONS) { HealthPermissionInfoScreen { onCompleteOnboarding(); nav.navigate(HOME) { popUpTo(WELCOME) { inclusive = true } } } }
            composable(HOME) { HomeScreen(steps, onMeasure = { nav.navigate(MEASURE) }) }
            composable(ACTIVITY) { ActivityScreen(steps) }
            composable(MEASURE) { MeasureScreen({ nav.navigate(HR_INTRO) }, { nav.navigate(BP) }, { nav.navigate(SLEEP) }) }
            composable(PROFILE) { ProfileScreen(onAbout = { nav.navigate(ABOUT) }) }
            composable(HR_INTRO) { HeartRateIntroScreen { nav.navigate(HR_MEASURE) } }
            composable(HR_MEASURE) { HeartRateMeasureScreen { bpm -> nav.navigate("hr_result/$bpm") { popUpTo(HR_MEASURE) { inclusive = true } } } }
            composable("hr_result/{bpm}") { entry ->
                val bpm = entry.arguments?.getString("bpm")?.toIntOrNull() ?: 0
                HeartRateResultScreen(
                    bpm = bpm,
                    onSave = { context -> scope.launch { repo.saveHeartRate(bpm, context) }; nav.navigate(MEASURE) { popUpTo(MEASURE) { inclusive = true } } },
                    onAgain = { nav.navigate(HR_MEASURE) }
                )
            }
            composable(BP) { BloodPressureScreen { sys, dia, pulse -> scope.launch { repo.saveBloodPressure(sys, dia, pulse) }; nav.popBackStack() } }
            composable(SLEEP) { SleepScreen() }
            composable(ABOUT) { AboutScreen() }
        }
    }
}
