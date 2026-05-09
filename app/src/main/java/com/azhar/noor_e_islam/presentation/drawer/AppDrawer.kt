package com.azhar.noor_e_islam.presentation.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.azhar.noor_e_islam.R
import com.azhar.noor_e_islam.core.navigation.Route
import com.azhar.noor_e_islam.core.ui.components.GeometricPatternBg
import com.azhar.noor_e_islam.core.ui.components.GoldButton
import com.azhar.noor_e_islam.ui.theme.Gold500
import com.azhar.noor_e_islam.ui.theme.NoorGradients

private data class DrawerItem(val labelRes: Int, val icon: ImageVector, val route: String)

private val items = listOf(
    DrawerItem(R.string.home,            Icons.Filled.Home,         Route.Home.route),
    DrawerItem(R.string.quran,           Icons.Filled.MenuBook,     Route.QuranList.route),
    DrawerItem(R.string.hadith,          Icons.Filled.AutoStories,  Route.Hadith.route),
    DrawerItem(R.string.calendar,        Icons.Filled.CalendarMonth,Route.Calendar.route),
    DrawerItem(R.string.incidents,       Icons.Filled.History,      Route.Incidents.route),
    DrawerItem(R.string.dua_collection,  Icons.Filled.Favorite,     Route.Dua.route),
    DrawerItem(R.string.learn_islam,     Icons.Filled.School,       Route.Learn.route),
    DrawerItem(R.string.stories,         Icons.Filled.AutoStories,  Route.Stories.route),
    DrawerItem(R.string.habits,          Icons.Filled.EmojiEvents,  Route.Habits.route),
    DrawerItem(R.string.bookmarks,       Icons.Filled.Bookmark,     Route.Bookmarks.route),
    DrawerItem(R.string.notes,           Icons.Filled.Note,         Route.Notes.route),
    DrawerItem(R.string.downloads,       Icons.Filled.Download,     Route.Downloads.route),
    DrawerItem(R.string.share_app,       Icons.Filled.Share,        "share_app"),
    DrawerItem(R.string.settings,        Icons.Filled.Settings,     Route.Settings.route),
    DrawerItem(R.string.help_support,    Icons.Filled.Help,         "help"),
    DrawerItem(R.string.about_us,        Icons.Filled.Info,         Route.About.route),
)

@Composable
fun AppDrawer(
    onClose: () -> Unit,
    onNavigate: (String) -> Unit,
) {
    ModalDrawerSheet(
        modifier = Modifier.fillMaxHeight().width(300.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        // Profile header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(NoorGradients.Emerald)
        ) {
            GeometricPatternBg(modifier = Modifier.fillMaxWidth().height(180.dp))
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text("Assalamu Alaikum", color = Gold500, style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(4.dp))
                Text("Noor Reader", color = androidx.compose.ui.graphics.Color.White, style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(12.dp))
                GoldButton(text = androidx.compose.ui.res.stringResource(R.string.upgrade_plan), onClick = { onNavigate("upgrade") })
            }
        }

        Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(vertical = 8.dp)) {
            items.forEach { item ->
                NavigationDrawerItem(
                    label = { Text(androidx.compose.ui.res.stringResource(item.labelRes)) },
                    selected = false,
                    icon = { Icon(item.icon, contentDescription = null) },
                    onClick = { onNavigate(item.route) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
            Spacer(Modifier.height(16.dp))

            // Footer: daily goal + quote
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(text = androidx.compose.ui.res.stringResource(R.string.daily_goal), style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(6.dp))
                LinearProgressIndicator(progress = { 0.6f }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(16.dp))
                Text(
                    text = androidx.compose.ui.res.stringResource(R.string.quote_of_the_day),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

