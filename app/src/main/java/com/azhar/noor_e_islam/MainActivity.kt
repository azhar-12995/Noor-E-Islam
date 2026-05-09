package com.azhar.noor_e_islam

import android.os.Bundle
import androidx.activity.ComponentActivity
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
        enableEdgeToEdge()

        // Splash composable will dismiss the system splash once it draws.
        splash.setKeepOnScreenCondition { false }

        setContent {
            NooreIslamTheme {
                NooreIslamApp()
            }
        }
    }
}
