package com.azhar.noor_e_islam.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.azhar.noor_e_islam.core.navigation.Route
import com.azhar.noor_e_islam.core.notifications.PrayerNotificationsBootstrapper
import com.azhar.noor_e_islam.presentation.drawer.AppDrawer
import com.azhar.noor_e_islam.presentation.navigation.NoorNavGraph
import com.azhar.noor_e_islam.presentation.scaffold.NoorScaffold
import kotlinx.coroutines.launch

/**
 * Root composable.
 *
 * Wires up:
 *  - NavHost ([NoorNavGraph])
 *  - Drawer ([AppDrawer])
 *  - Bottom navigation + top bar via [NoorScaffold] (only shown on bottom-nav routes)
 */
@Composable
fun NooreIslamApp(
    pendingRoute: String? = null,
    onPendingRouteConsumed: () -> Unit = {},
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showShell = Route.bottomNav.any { it.route == currentRoute }

    // Handle a notification-tap deep link (e.g. "hadith" from the daily-hadith
    // worker). Navigates once per delivered route, then clears the flag.
    LaunchedEffect(pendingRoute) {
        val target = pendingRoute ?: return@LaunchedEffect
        navController.navigate(target) {
            launchSingleTop = true
        }
        onPendingRouteConsumed()
    }

    // Ask for all runtime permissions on first launch in a single batched
    // request. Two separate launchers racing each other on parallel
    // LaunchedEffects causes Android to drop the second prompt (the system
    // only shows one permission dialog at a time per activity), which is
    // why POST_NOTIFICATIONS was never appearing.
    //
    //   - ACCESS_FINE/COARSE_LOCATION → prayer times, qibla, notifications
    //   - POST_NOTIFICATIONS (API 33+) → daily hadith + prayer reminders
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        // If location was just granted, fetch a one-shot fix and arm the
        // prayer-time alarms straight away — otherwise the scheduler would
        // have to wait until the user opens the Prayer Times screen.
        val locationOk = grants[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            grants[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (locationOk) {
            PrayerNotificationsBootstrapper.kickoff(context)
        }
    }
    LaunchedEffect(Unit) {
        val toRequest = buildList {
            val locationGranted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            if (!locationGranted) {
                add(Manifest.permission.ACCESS_FINE_LOCATION)
                add(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val notifGranted = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
                if (!notifGranted) add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        if (toRequest.isNotEmpty()) {
            permissionLauncher.launch(toRequest.toTypedArray())
        } else {
            // All permissions already granted on a previous launch — make sure
            // prayer alarms are armed for the next 24h (no-op if already done).
            PrayerNotificationsBootstrapper.kickoff(context)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = showShell,
        drawerContent = {
            AppDrawer(
                onClose = { scope.launch { drawerState.close() } },
                onNavigate = { route ->
                    scope.launch { drawerState.close() }
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                }
            )
        }
    ) {
        NoorScaffold(
            navController = navController,
            showShell = showShell,
            onMenuClick = { scope.launch { drawerState.open() } },
            modifier = Modifier.fillMaxSize().statusBarsPadding()
        ) { padding ->
            NoorNavGraph(
                navController = navController,
                contentPadding = padding,
                onOpenMenu = { scope.launch { drawerState.open() } },
            )
        }
    }
}

