package com.azhar.noor_e_islam.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.azhar.noor_e_islam.core.navigation.Route
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
fun NooreIslamApp() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showShell = Route.bottomNav.any { it.route == currentRoute }

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

