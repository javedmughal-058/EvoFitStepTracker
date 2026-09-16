package com.evolixtechnologies.evofit.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Typography
import com.evolixtechnologies.evofit.R

val EvoGreen = Color(0xFF42D915)
val EvoGreenDark = Color(0xFF10921F)
val EvoBlack = Color(0xFF080B1A)
val EvoSurface = Color(0xFFF5FAFC)
val EvoBorder = Color(0xFFE7EDF3)
val EvoMuted = Color(0xFF727A99)
val EvoRed = Color(0xFFFF5C65)
val EvoBlue = Color(0xFF1677F2)
val EvoPurple = Color(0xFF8657F5)
val EvoOrange = Color(0xFFFF6B12)
val EvoSoftGreen = Color(0xFFE9FBE6)

private val Urbanist = FontFamily(
    Font(R.font.urbanist_regular, FontWeight.Normal),
    Font(R.font.urbanist_medium, FontWeight.Medium),
    Font(R.font.urbanist_semibold, FontWeight.SemiBold),
    Font(R.font.urbanist_bold, FontWeight.Bold),
    Font(R.font.urbanist_extrabold, FontWeight.ExtraBold)
)

private val Light = lightColorScheme(
    primary = EvoGreenDark,
    onPrimary = Color.White,
    primaryContainer = EvoSoftGreen,
    onPrimaryContainer = Color(0xFF086A12),
    background = EvoSurface,
    onBackground = EvoBlack,
    surface = Color.White,
    onSurface = EvoBlack,
    surfaceVariant = Color(0xFFF0F5F9),
    onSurfaceVariant = EvoMuted,
    outline = EvoBorder,
    secondary = EvoGreenDark,
    secondaryContainer = Color(0xFFDDFCCF),
    onSecondaryContainer = Color(0xFF163D00)
)

private val Dark = darkColorScheme(
    primary = EvoGreen,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF275900),
    onPrimaryContainer = Color(0xFFE7FFD9),
    background = Color(0xFF101210),
    onBackground = Color(0xFFF5F7F4),
    surface = Color(0xFF181B18),
    onSurface = Color(0xFFF5F7F4),
    surfaceVariant = Color(0xFF263124),
    onSurfaceVariant = Color(0xFFD4DEC9),
    outline = Color(0xFF2A2E2A),
    secondary = EvoGreen,
    secondaryContainer = Color(0xFF275900),
    onSecondaryContainer = Color(0xFFE7FFD9)
)

private val EvoTypography = Typography(
    displayLarge = TextStyle(fontFamily = Urbanist, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold),
    headlineLarge = TextStyle(fontFamily = Urbanist, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold),
    headlineMedium = TextStyle(fontFamily = Urbanist, fontSize = 18.sp, fontWeight = FontWeight.Bold),
    titleLarge = TextStyle(fontFamily = Urbanist, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 22.sp),
    titleMedium = TextStyle(fontFamily = Urbanist, fontSize = 17.sp, fontWeight = FontWeight.Bold, lineHeight = 21.sp),
    titleSmall = TextStyle(fontFamily = Urbanist, fontSize = 16.sp, fontWeight = FontWeight.Bold, lineHeight = 20.sp),
    bodyLarge = TextStyle(fontFamily = Urbanist, fontSize = 16.sp, fontWeight = FontWeight.Normal, lineHeight = 20.sp),
    bodyMedium = TextStyle(fontFamily = Urbanist, fontSize = 14.sp, fontWeight = FontWeight.Medium, lineHeight = 18.sp),
    bodySmall = TextStyle(fontFamily = Urbanist, fontSize = 12.sp, fontWeight = FontWeight.Medium, lineHeight = 16.sp),
    labelLarge = TextStyle(fontFamily = Urbanist, fontSize = 15.sp, fontWeight = FontWeight.Bold),
    labelMedium = TextStyle(fontFamily = Urbanist, fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
    labelSmall = TextStyle(fontFamily = Urbanist, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
)

@Composable
fun EvoFitTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) Dark else Light,
        typography = EvoTypography,
        content = content
    )
}
