package com.azhar.noor_e_islam.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = Emerald600,
    onPrimary = Color.White,
    primaryContainer = Emerald100,
    onPrimaryContainer = Emerald900,
    secondary = Gold500,
    onSecondary = Color.Black,
    secondaryContainer = Gold100,
    onSecondaryContainer = Color(0xFF3D2D00),
    tertiary = Emerald400,
    onTertiary = Color.White,
    background = Ivory,
    onBackground = Charcoal,
    surface = Color.White,
    onSurface = Charcoal,
    surfaceVariant = IvoryDim,
    onSurfaceVariant = Slate,
    outline = MutedGreen,
    error = ErrorRed,
    onError = Color.White,
)

private val DarkColors = darkColorScheme(
    primary = Emerald300,
    onPrimary = Emerald900,
    primaryContainer = Emerald700,
    onPrimaryContainer = Emerald50,
    secondary = Gold300,
    onSecondary = Charcoal,
    secondaryContainer = Gold700,
    onSecondaryContainer = Gold100,
    tertiary = Emerald400,
    onTertiary = Color.White,
    background = Midnight,
    onBackground = Ivory,
    surface = Charcoal,
    onSurface = Ivory,
    surfaceVariant = Color(0xFF223330),
    onSurfaceVariant = Emerald100,
    outline = MutedGreen,
    error = ErrorRed,
    onError = Color.White,
)

@Composable
fun NooreIslamTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Disabled by default so brand colors are always used. Pass true to enable Material You.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Deep emerald status bar — visually separated from content, premium dark feel.
            val statusBar = if (darkTheme) Midnight else Emerald900
            window.statusBarColor = statusBar.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false  // dark bg → white icons
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = NoorTypography,
        shapes = NoorShapes,
        content = content
    )
}