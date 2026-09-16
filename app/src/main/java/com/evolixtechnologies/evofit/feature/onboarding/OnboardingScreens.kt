package com.evolixtechnologies.evofit.feature.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.evolixtechnologies.evofit.R
import com.evolixtechnologies.evofit.core.designsystem.EvoBlue
import com.evolixtechnologies.evofit.core.designsystem.EvoCard
import com.evolixtechnologies.evofit.core.designsystem.EvoFilterChip
import com.evolixtechnologies.evofit.core.designsystem.EvoGreen
import com.evolixtechnologies.evofit.core.designsystem.EvoMuted
import com.evolixtechnologies.evofit.core.designsystem.EvoPrimaryButton
import com.evolixtechnologies.evofit.core.designsystem.EvoPurple
import com.evolixtechnologies.evofit.core.designsystem.EvoRed
import com.evolixtechnologies.evofit.core.designsystem.EvoOrange
import com.evolixtechnologies.evofit.core.designsystem.EvoTopBar
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.math.sin

data class ProfileSetup(
    val name: String,
    val age: String,
    val height: String,
    val weight: String,
    val gender: String
)

data class DailyGoals(
    val steps: Int = 8000,
    val distanceKm: String = "4.0",
    val activeMinutes: String = "45",
    val calories: String = "300"
)

@Composable
fun WelcomeScreen(onNext: () -> Unit) {
    val slides = listOf(
        OnboardSlide("Small Steps\nBig Progress", "Track every movement and build a healthier routine with EvoFit.", Icons.Default.DirectionsWalk, EvoGreen),
        OnboardSlide("Know Your Day", "See live steps, distance, calories, and active time as your phone records movement.", Icons.Default.DirectionsRun, EvoBlue),
        OnboardSlide("Wellness Checks", "Log heart rate, blood pressure, sleep, and activity in one focused measure hub.", Icons.Default.Favorite, EvoRed),
        OnboardSlide("Stay Consistent", "Set goals, review activity, and keep your health profile ready for better insights.", Icons.Default.MonitorHeart, EvoPurple)
    )
    val pager = rememberPagerState(pageCount = { slides.size })

    Column(Modifier.fillMaxSize().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onNext) { Text("Skip") }
        }
        Spacer(Modifier.height(12.dp))
        HorizontalPager(state = pager, modifier = Modifier.fillMaxWidth().weight(1f)) { page ->
            val slide = slides[page]
            Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                WalkIllustration(slide.icon, slide.tint)
                Spacer(Modifier.height(28.dp))
                Text(
                    slide.title,
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    slide.subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = EvoMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 14.dp)
                )
            }
        }
        Spacer(Modifier.height(18.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            repeat(slides.size) {
                Box(
                    Modifier
                        .size(if (it == pager.currentPage) 18.dp else 7.dp, 7.dp)
                        .background(if (it == pager.currentPage) Color.Black else Color(0xFFD5D9D1), CircleShape)
                )
            }
        }
        Spacer(Modifier.height(22.dp))
        EvoPrimaryButton("Get Started", onNext)
    }
}

@Composable
fun PersonalSetupScreen(initial: ProfileSetup, onNext: (ProfileSetup) -> Unit) {
    var name by remember { mutableStateOf(initial.name.ifBlank { "" }) }
    var age by remember { mutableStateOf(initial.age.ifBlank { "26" }) }
    var height by remember { mutableStateOf(initial.height.ifBlank { "178" }) }
    var weight by remember { mutableStateOf(initial.weight.ifBlank { "74" }) }
    var gender by remember { mutableStateOf(initial.gender.ifBlank { "Male" }) }

    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        StepProgress(1, 3)
        Spacer(Modifier.height(14.dp))
        EvoTopBar("Tell us about you", subtitle = "This helps personalize your activity and health insights.")
        Spacer(Modifier.height(8.dp))
        NameField(name) { name = it.take(40) }
        SetupField("Age", age, "") { age = it }
        HeightSetupField(height) { height = it }
        SetupField("Weight", weight, "kg") { weight = it }
        SetupChoice("Gender", listOf("Male", "Female", "Other"), gender) { gender = it }
        Spacer(Modifier.weight(1f))
        EvoPrimaryButton(
            "Continue",
            { onNext(ProfileSetup(name.trim(), age, height, weight, gender)) },
            enabled = name.trim().length >= 2
        )
    }
}

@Composable
fun GoalSetupScreen(initialGoals: DailyGoals, onNext: (DailyGoals) -> Unit) {
    var goal by remember { mutableIntStateOf(initialGoals.steps.coerceIn(1000, 30000)) }
    var distance by remember { mutableStateOf(initialGoals.distanceKm.ifBlank { "4.0" }) }
    var activeTime by remember { mutableStateOf(initialGoals.activeMinutes.ifBlank { "45" }) }
    var calories by remember { mutableStateOf(initialGoals.calories) }
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        StepProgress(2, 3)
        Spacer(Modifier.height(14.dp))
        EvoTopBar("Your daily goal", subtitle = "Set a goal to stay motivated. You can change this anytime.")
        EvoCard {
            Row(
                Modifier.fillMaxWidth().padding(22.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FilledTonalIconButton(onClick = { goal = (goal - 500).coerceAtLeast(1000) }) {
                    Icon(Icons.Default.Remove, null)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.DirectionsWalk, null, tint = EvoGreen)
                        Text("Daily Steps", style = MaterialTheme.typography.titleSmall)
                    }
                    Text("%,d".format(goal), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Recommended", style = MaterialTheme.typography.bodySmall, color = EvoMuted)
                }
                FilledTonalIconButton(onClick = { goal = (goal + 500).coerceAtMost(30000) }) {
                    Icon(Icons.Default.Add, null)
                }
            }
        }
        GoalRow("Distance goal", "Set a daily distance target.", Icons.Default.LocationOn, EvoGreen, distance, "km") { distance = it.filter { char -> char.isDigit() || char == '.' }.take(4) }
        GoalRow("Active time", "Set a daily active time goal.", Icons.Default.Timer, EvoBlue, activeTime, "min") { activeTime = it.filter(Char::isDigit).take(3) }
        GoalRow("Calories goal (optional)", "Set a daily calorie burn goal.", Icons.Default.LocalFireDepartment, EvoOrange, calories, "kcal") { calories = it.filter(Char::isDigit).take(4) }
        Spacer(Modifier.weight(1f))
        EvoPrimaryButton("Continue", { onNext(DailyGoals(goal, distance.ifBlank { "4.0" }, activeTime.ifBlank { "45" }, calories)) })
    }
}

@Composable
fun HealthPermissionInfoScreen(onFinish: () -> Unit) {
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        StepProgress(3, 3)
        Spacer(Modifier.height(14.dp))
        EvoTopBar("Connect your health data", subtitle = "EvoFit can read your fitness and wellness data using Android permissions and Health Connect.")
        PermissionRow(Icons.Default.DirectionsWalk, "Steps", EvoGreen)
        PermissionRow(Icons.Default.Hotel, "Sleep", EvoBlue)
        PermissionRow(Icons.Default.DirectionsRun, "Exercise", EvoPurple)
        PermissionRow(Icons.Default.Favorite, "Heart rate", EvoRed)
        PermissionRow(Icons.Default.MonitorHeart, "Blood pressure", EvoBlue)
        Spacer(Modifier.weight(1f))
        EvoPrimaryButton("Connect Health Connect", onFinish)
        TextButton(onClick = onFinish, modifier = Modifier.fillMaxWidth()) { Text("Skip for now") }
        Text(
            "You can manage permissions anytime in settings.",
            style = MaterialTheme.typography.bodySmall,
            color = EvoMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun NameField(value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        modifier = Modifier.fillMaxWidth().height(58.dp),
        singleLine = true,
        label = { Text("Full name") },
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
private fun SetupField(label: String, value: String, unit: String, onChange: (String) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(label, modifier = Modifier.width(72.dp), style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = value,
            onValueChange = { onChange(it.filter(Char::isDigit).take(3)) },
            modifier = Modifier.weight(1f).height(56.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(10.dp),
            trailingIcon = { if (unit.isNotBlank()) Text(unit, modifier = Modifier.padding(end = 8.dp), color = EvoMuted) }
        )
    }
}

@Composable
private fun HeightSetupField(valueCm: String, onChangeCm: (String) -> Unit) {
    var unit by remember { mutableStateOf("cm") }
    var cm by remember(valueCm) { mutableStateOf(valueCm.ifBlank { "178" }) }
    val initialCm = valueCm.toIntOrNull() ?: 178
    val totalInches = (initialCm / 2.54).roundToInt()
    var feet by remember(valueCm) { mutableStateOf((totalInches / 12).toString()) }
    var inches by remember(valueCm) { mutableStateOf((totalInches % 12).toString()) }

    fun saveFeetInches(nextFeet: String = feet, nextInches: String = inches) {
        val ft = nextFeet.toIntOrNull() ?: 0
        val inch = nextInches.toIntOrNull() ?: 0
        val normalizedInches = ft * 12 + inch.coerceIn(0, 11)
        if (normalizedInches > 0) {
            onChangeCm((normalizedInches * 2.54).roundToInt().toString())
        }
    }

    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Height", modifier = Modifier.width(72.dp), style = MaterialTheme.typography.bodyMedium)
            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                EvoFilterChip(
                    selected = unit == "cm",
                    text = "cm",
                    onClick = {
                        unit = "cm"
                        cm = valueCm.ifBlank { cm }
                    },
                    modifier = Modifier.weight(1f)
                )
                EvoFilterChip(
                    selected = unit == "ft",
                    text = "ft / in",
                    onClick = {
                        unit = "ft"
                        val currentCm = valueCm.toIntOrNull() ?: cm.toIntOrNull() ?: 178
                        val currentInches = (currentCm / 2.54).roundToInt()
                        feet = (currentInches / 12).toString()
                        inches = (currentInches % 12).toString()
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        if (unit == "cm") {
            SetupField("", cm, "cm") {
                cm = it
                if (it.isNotBlank()) onChangeCm(it)
            }
        } else {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Spacer(Modifier.width(72.dp))
                OutlinedTextField(
                    value = feet,
                    onValueChange = {
                        feet = it.filter(Char::isDigit).take(1)
                        saveFeetInches(nextFeet = feet)
                    },
                    modifier = Modifier.weight(1f).height(56.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(10.dp),
                    trailingIcon = { Text("ft", modifier = Modifier.padding(end = 8.dp), color = EvoMuted) }
                )
                OutlinedTextField(
                    value = inches,
                    onValueChange = {
                        val digits = it.filter(Char::isDigit)
                        val nextValue = if (digits.length > 2) digits.takeLast(1) else digits
                        inches = nextValue.toIntOrNull()?.coerceAtMost(11)?.toString() ?: ""
                        saveFeetInches(nextInches = inches)
                    },
                    modifier = Modifier.weight(1f).height(56.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(10.dp),
                    trailingIcon = { Text("in", modifier = Modifier.padding(end = 8.dp), color = EvoMuted) }
                )
            }
        }
    }
}

@Composable
private fun SetupChoice(label: String, options: List<String>, selected: String, onSelected: (String) -> Unit) {
    var value by remember { mutableStateOf(selected) }
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(label, modifier = Modifier.width(72.dp), style = MaterialTheme.typography.bodyMedium)
        Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach {
                EvoFilterChip(
                    selected = value == it,
                    text = it,
                    onClick = { value = it; onSelected(it) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StepProgress(step: Int, total: Int) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Step $step of $total", style = MaterialTheme.typography.bodyLarge, color = EvoMuted)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(total) { index ->
                Box(
                    Modifier
                        .size(width = 74.dp, height = 10.dp)
                        .background(if (index < step) EvoGreen else Color(0xFFE5ECF2), RoundedCornerShape(16.dp))
                )
            }
        }
    }
}

@Composable
private fun GoalRow(label: String, subtitle: String, icon: ImageVector, tint: Color, value: String, unit: String, onChange: (String) -> Unit) {
    EvoCard {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(Modifier.size(58.dp).background(tint.copy(alpha = .12f), RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = tint)
            }
            Column(Modifier.weight(1f)) {
                Text(label, style = MaterialTheme.typography.titleSmall)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = EvoMuted)
            }
            OutlinedTextField(
                value = value,
                onValueChange = onChange,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                trailingIcon = { Text(unit, modifier = Modifier.padding(end = 8.dp), color = EvoMuted) },
                modifier = Modifier.width(118.dp),
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
private fun PermissionRow(icon: ImageVector, title: String, tint: Color) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Icon(icon, null, tint = tint)
        Text(title, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.weight(1f))
        Icon(Icons.Default.Check, null, tint = EvoGreen, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun WalkIllustration(icon: ImageVector, tint: Color) {
    Box(Modifier.fillMaxWidth().height(180.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(Color(0xFFD9F7D4), 34f, Offset(size.width * .2f, size.height * .65f))
            drawCircle(Color(0xFFD9F7D4), 28f, Offset(size.width * .8f, size.height * .58f))
            drawLine(Color(0xFFBBDDB8), Offset(size.width * .2f, size.height * .42f), Offset(size.width * .2f, size.height * .88f), 4f)
            drawLine(Color(0xFFBBDDB8), Offset(size.width * .8f, size.height * .34f), Offset(size.width * .8f, size.height * .88f), 4f)
        }
        Icon(icon, null, tint = tint, modifier = Modifier.size(92.dp))
    }
}

private data class OnboardSlide(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val tint: Color
)
