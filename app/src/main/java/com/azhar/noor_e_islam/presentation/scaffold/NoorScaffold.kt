package com.azhar.noor_e_islam.presentation.scaffold

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.azhar.noor_e_islam.R
import com.azhar.noor_e_islam.core.navigation.Route

private data class TabItem(val route: Route, val labelRes: Int, val icon: ImageVector)

private val tabs = listOf(
    TabItem(Route.Home,      R.string.home,    Icons.Filled.Home),
    TabItem(Route.QuranList, R.string.quran,   Icons.Filled.MenuBook),
    TabItem(Route.Learn,     R.string.learn,   Icons.Filled.AutoStories),
    TabItem(Route.Calendar,  R.string.calendar,Icons.Filled.CalendarMonth),
    TabItem(Route.Profile,   R.string.profile, Icons.Filled.Person),
)

/** Routes that render their own [TopAppBar] (so we hide the global one). */
private val routesWithOwnTopBar = setOf(
    Route.QuranList.route,
    Route.Home.route,        // hero header
    Route.Profile.route,     // hero header
    Route.Hadith.route,      // ornamental hadith card with back/title
    Route.Calendar.route,    // hero with hijri date
    Route.Stories.route,     // back + tabs
    Route.Incidents.route,   // hero image
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoorScaffold(
    navController: NavHostController,
    showShell: Boolean,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit,
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showTopBar = showShell && currentRoute !in routesWithOwnTopBar

    Scaffold(
        modifier = modifier,
        topBar = {
            AnimatedVisibility(
                visible = showTopBar,
                enter = slideInVertically { -it } + fadeIn(),
                exit = slideOutVertically { -it } + fadeOut()
            ) {
                CenterAlignedTopAppBar(
                    title = { Text(text = stringForRoute(currentRoute)) },
                    navigationIcon = {
                        IconButton(onClick = onMenuClick) {
                            Icon(Icons.Outlined.Menu, contentDescription = "Menu")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    )
                )
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = showShell,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut()
            ) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    tabs.forEach { tab ->
                        val selected = backStackEntry?.destination?.hierarchy?.any { it.route == tab.route.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(tab.route.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = null) },
                            label = { Text(androidx.compose.ui.res.stringResource(tab.labelRes)) }
                        )
                    }
                }
            }
        }
    ) { padding -> content(padding) }
}

@Composable
private fun stringForRoute(route: String?): String {
    val res = when (route) {
        Route.Home.route      -> R.string.app_name
        Route.QuranList.route -> R.string.quran
        Route.Learn.route     -> R.string.learn_islam
        Route.Calendar.route  -> R.string.calendar
        Route.Profile.route   -> R.string.profile
        else                  -> R.string.app_name
    }
    return androidx.compose.ui.res.stringResource(res)
}

/** Exposed for screens that want to align their own top app bar to the global shell list. */
fun routeOwnsTopBar(route: String?): Boolean = route in routesWithOwnTopBar
