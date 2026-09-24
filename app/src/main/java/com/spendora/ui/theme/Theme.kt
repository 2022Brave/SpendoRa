package com.spendora.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class SpendoraThemeMode {
    BLACK_PURPLE,
    WHITE_PURPLE
}

@Immutable
data class SpendoraCustomColors(
    val border: Color,
    val borderGlow: Color,
    val surfaceHighlight: Color,
    val cardBackground: Color,
    val textMuted: Color,
    val financeGreen: Color,
    val financeRed: Color,
    val financeBlue: Color,
    val financeAmber: Color,
    val isDark: Boolean
)

val LocalSpendoraColors = staticCompositionLocalOf {
    SpendoraCustomColors(
        border = MidnightBorder,
        borderGlow = MidnightBorderGlow,
        surfaceHighlight = MidnightSurfaceHighlight,
        cardBackground = MidnightSurface,
        textMuted = TextMutedDark,
        financeGreen = FinanceGreen,
        financeRed = FinanceRed,
        financeBlue = FinanceBlue,
        financeAmber = FinanceAmber,
        isDark = true
    )
}

private val DarkColorScheme = darkColorScheme(
    primary = PurplePrimary,
    onPrimary = Color.White,
    primaryContainer = PurpleDark,
    onPrimaryContainer = LavenderSoft,
    secondary = LavenderLight,
    onSecondary = MidnightBlack,
    secondaryContainer = MidnightSurfaceHighlight,
    onSecondaryContainer = LavenderSoft,
    tertiary = PurpleElectric,
    onTertiary = Color.White,
    background = MidnightBlack,
    onBackground = TextPrimaryDark,
    surface = MidnightSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = MidnightSurfaceElevated,
    onSurfaceVariant = TextSecondaryDark,
    outline = MidnightBorder,
    outlineVariant = MidnightBorderGlow
)

private val LightColorScheme = lightColorScheme(
    primary = PurplePrimary,
    onPrimary = Color.White,
    primaryContainer = LavenderSoft,
    onPrimaryContainer = PurpleDark,
    secondary = PurpleElectric,
    onSecondary = Color.White,
    secondaryContainer = LightSurfaceElevated,
    onSecondaryContainer = TextPrimaryLight,
    tertiary = LavenderLight,
    onTertiary = MidnightBlack,
    background = LightBackground,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightSurfaceElevated,
    onSurfaceVariant = TextSecondaryLight,
    outline = LightBorder,
    outlineVariant = LavenderLight
)

@Composable
fun SpendoraTheme(
    themeMode: SpendoraThemeMode = SpendoraThemeMode.BLACK_PURPLE,
    content: @Composable () -> Unit
) {
    val isDark = themeMode == SpendoraThemeMode.BLACK_PURPLE
    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    val customColors = if (isDark) {
        SpendoraCustomColors(
            border = MidnightBorder,
            borderGlow = MidnightBorderGlow,
            surfaceHighlight = MidnightSurfaceHighlight,
            cardBackground = MidnightSurface,
            textMuted = TextMutedDark,
            financeGreen = FinanceGreen,
            financeRed = FinanceRed,
            financeBlue = FinanceBlue,
            financeAmber = FinanceAmber,
            isDark = true
        )
    } else {
        SpendoraCustomColors(
            border = LightBorder,
            borderGlow = LavenderLight,
            surfaceHighlight = LightSurfaceElevated,
            cardBackground = LightSurface,
            textMuted = TextMutedLight,
            financeGreen = FinanceGreen,
            financeRed = FinanceRed,
            financeBlue = FinanceBlue,
            financeAmber = FinanceAmber,
            isDark = false
        )
    }

    CompositionLocalProvider(LocalSpendoraColors provides customColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

object SpendoraTheme {
    val customColors: SpendoraCustomColors
        @Composable
        get() = LocalSpendoraColors.current
}
