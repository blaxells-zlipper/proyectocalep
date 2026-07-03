package com.example.incidencias.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val ColorScheme = darkColorScheme(
    primary = Color(0xFFB7F35B),
    onPrimary = Color(0xFF17210A),
    primaryContainer = Color(0xFF334A17),
    onPrimaryContainer = Color(0xFFE3FFC1),
    secondary = Color(0xFFFFB36B),
    onSecondary = Color(0xFF2F1700),
    secondaryContainer = Color(0xFF5A3106),
    onSecondaryContainer = Color(0xFFFFD7AD),
    tertiary = Color(0xFF72D8FF),
    onTertiary = Color(0xFF002635),
    tertiaryContainer = Color(0xFF11465B),
    background = Color(0xFF09111F),
    onBackground = Color(0xFFEAF0F7),
    surface = Color(0xFF111B2D),
    onSurface = Color(0xFFEAF0F7),
    surfaceVariant = Color(0xFF243149),
    onSurfaceVariant = Color(0xFFC3CBD8),
    outline = Color(0xFF647088),
    error = Color(0xFFFF8A80),
    onError = Color(0xFF3B0806)
)

private val AppTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Black,
        fontSize = 32.sp,
        lineHeight = 36.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 26.sp,
        lineHeight = 31.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 21.sp,
        lineHeight = 27.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 23.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        lineHeight = 18.sp
    )
)

@Composable
fun IncidenciasTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColorScheme,
        typography = AppTypography,
        content = content
    )
}
