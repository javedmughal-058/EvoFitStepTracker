package com.evolixtechnologies.evofit.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Typography

val EvoGreen = Color(0xFF63E600)
val EvoGreenDark = Color(0xFF46B900)
val EvoBlack = Color(0xFF111411)
val EvoSurface = Color(0xFFF7F8F6)
val EvoBorder = Color(0xFFE5E8E2)
val EvoMuted = Color(0xFF737871)
val EvoRed = Color(0xFFFF5C65)
val EvoBlue = Color(0xFF4B8DFF)
val EvoPurple = Color(0xFF786BF2)

private val Light = lightColorScheme(
    primary = EvoGreenDark,
    onPrimary = Color.White,
    background = EvoSurface,
    onBackground = EvoBlack,
    surface = Color.White,
    onSurface = EvoBlack,
    outline = EvoBorder,
    secondary = EvoBlue
)

private val Dark = darkColorScheme(
    primary = EvoGreen,
    onPrimary = Color.Black,
    background = Color(0xFF101210),
    onBackground = Color(0xFFF5F7F4),
    surface = Color(0xFF181B18),
    onSurface = Color(0xFFF5F7F4),
    outline = Color(0xFF2A2E2A),
    secondary = Color(0xFF7BA9FF)
)

private val EvoTypography = Typography(
    titleLarge = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
    titleMedium = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
    titleSmall = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
    bodyLarge = TextStyle(fontSize = 14.sp),
    bodyMedium = TextStyle(fontSize = 12.sp),
    bodySmall = TextStyle(fontSize = 10.sp),
    labelLarge = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
    labelMedium = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium),
    labelSmall = TextStyle(fontSize = 10.sp)
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
