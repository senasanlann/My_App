package com.example.my_app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = IndigoPrimaryLight,
    onPrimary = IndigoOnPrimaryLight,
    primaryContainer = IndigoContainerLight,
    onPrimaryContainer = IndigoOnContainerLight,
    secondary = EyebrowRed,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF7DDE2),
    onSecondaryContainer = BurgundyDeep,
    tertiary = IndigoPrimaryLight,
    onTertiary = Color.White,
    background = CreamLight,
    onBackground = WarmInk,
    surface = CreamSurface,
    onSurface = WarmInk,
    surfaceVariant = CreamSurfaceVariant,
    onSurfaceVariant = WarmInkMuted,
    outline = WarmOutline,
    outlineVariant = WarmOutlineStrong,
    surfaceDim = CreamContainerLow,
    surfaceBright = CreamLight,
    surfaceContainerLowest = CreamContainerLowest,
    surfaceContainerLow = CreamContainerLow,
    surfaceContainer = CreamContainer,
    surfaceContainerHigh = CreamContainerHigh,
    surfaceContainerHighest = CreamContainerHighest,
    inverseSurface = WarmInk,
    inverseOnSurface = CreamLight,
    inversePrimary = IndigoPrimaryDark
)

private val DarkColorScheme = darkColorScheme(
    primary = IndigoPrimaryDark,
    onPrimary = IndigoOnPrimaryDark,
    primaryContainer = IndigoContainerDark,
    onPrimaryContainer = IndigoOnContainerDark,
    secondary = EyebrowRedDark,
    onSecondary = DarkBg,
    secondaryContainer = Color(0xFF4A252D),
    onSecondaryContainer = Color(0xFFF7DDE2),
    tertiary = IndigoPrimaryDark,
    onTertiary = IndigoOnPrimaryDark,
    background = DarkBg,
    onBackground = WarmOnDark,
    surface = DarkSurface,
    onSurface = WarmOnDark,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = WarmOnDarkMuted,
    outline = DarkOutline,
    outlineVariant = DarkOutlineStrong,
    surfaceDim = DarkContainerLow,
    surfaceBright = DarkSurfaceVariant,
    surfaceContainerLowest = DarkContainerLowest,
    surfaceContainerLow = DarkContainerLow,
    surfaceContainer = DarkContainer,
    surfaceContainerHigh = DarkContainerHigh,
    surfaceContainerHighest = DarkContainerHighest,
    inverseSurface = CreamLight,
    inverseOnSurface = WarmInk,
    inversePrimary = IndigoPrimaryLight
)

@Composable
fun My_AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}
