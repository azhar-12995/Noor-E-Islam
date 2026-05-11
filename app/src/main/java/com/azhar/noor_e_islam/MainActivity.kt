package com.azhar.noor_e_islam

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.azhar.noor_e_islam.presentation.NooreIslamApp
import com.azhar.noor_e_islam.ui.theme.NooreIslamTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install Android 12 splash screen and let it stay briefly while VM resolves auth.
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Force an emerald, dark-icon-friendly status bar on ALL Android versions
        // (the old window.statusBarColor approach is deprecated/ignored on API 35+
        // and inconsistent on some vendor OEM ROMs).
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(EMERALD_900_ARGB),
            navigationBarStyle = SystemBarStyle.light(Color.WHITE, Color.BLACK),
        )

        // Splash composable will dismiss the system splash once it draws.
        splash.setKeepOnScreenCondition { false }

        setContent {
            NooreIslamTheme {
                NooreIslamApp()
            }
        }
    }

    companion object {
        /** Hex matches ui.theme.Emerald900 = 0xFF062B1F. */
        private const val EMERALD_900_ARGB = 0xFF062B1F.toInt()
    }
}
