package com.azhar.noor_e_islam

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.azhar.noor_e_islam.core.notifications.HadithDailyWorker
import com.azhar.noor_e_islam.presentation.NooreIslamApp
import com.azhar.noor_e_islam.ui.theme.NooreIslamTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Pending deep-link route extracted from the launching Intent (e.g. from a
    // notification tap). Observed by the Compose tree so the nav graph can
    // jump straight to the requested screen.
    private var pendingRoute by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(EMERALD_900_ARGB),
            navigationBarStyle = SystemBarStyle.light(Color.WHITE, Color.BLACK),
        )

        splash.setKeepOnScreenCondition { false }

        pendingRoute = extractRoute(intent)

        setContent {
            NooreIslamTheme {
                NooreIslamApp(
                    pendingRoute = pendingRoute,
                    onPendingRouteConsumed = { pendingRoute = null },
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        extractRoute(intent)?.let { pendingRoute = it }
    }

    private fun extractRoute(intent: Intent?): String? {
        if (intent == null) return null
        intent.getStringExtra(HadithDailyWorker.EXTRA_OPEN_ROUTE)?.let { return it }
        // Also accept the deep-link URI form ("noor://<route>") as a fallback.
        return intent.data?.takeIf { it.scheme == "noor" }?.host
    }

    companion object {
        private const val EMERALD_900_ARGB = 0xFF062B1F.toInt()
    }
}


