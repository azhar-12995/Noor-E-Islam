package com.azhar.noor_e_islam.presentation.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.azhar.noor_e_islam.BuildConfig
import com.azhar.noor_e_islam.R
import com.azhar.noor_e_islam.core.navigation.Route
import com.azhar.noor_e_islam.core.ui.components.GeometricPatternBg
import com.azhar.noor_e_islam.ui.theme.Emerald100
import com.azhar.noor_e_islam.ui.theme.Emerald900
import com.azhar.noor_e_islam.ui.theme.Gold500
import com.azhar.noor_e_islam.ui.theme.NoorGradients

/* ---------------- Data ---------------- */

private data class DrawerEntry(
    val label: String,
    val icon: ImageVector,
    val route: String,
)

private data class DrawerSection(
    val title: String,
    val items: List<DrawerEntry>,
)

/**
 * Drawer contains items that DO NOT appear on the Home screen's quick access
 * or bottom navigation. Home already covers: Home/Quran/Learn/Calendar/Profile
 * (bottom nav) + Hadith/Dua/Stories/Incidents/Habits/Bookmarks/Notes/Downloads
 * (quick access) + Qibla/Prayer Times (top cards).
 *
 * So the drawer focuses on personal, preferences and information items only.
 */
private val sections = listOf(
    DrawerSection(
        title = "Account",
        items = listOf(
            DrawerEntry("My Profile",       Icons.Filled.Person,        Route.Profile.route),
            DrawerEntry("Reading Progress", Icons.Filled.Insights,      Route.Progress.route),
        ),
    ),
    DrawerSection(
        title = "Preferences",
        items = listOf(
            DrawerEntry("Settings",         Icons.Filled.Settings,      Route.Settings.route),
        ),
    ),
    DrawerSection(
        title = "More",
        items = listOf(
            DrawerEntry("Share App",        Icons.Filled.Share,                 "share_app"),
            DrawerEntry("Feedback",         Icons.Filled.Feedback,              Route.Feedback.create()),
            DrawerEntry("Help & Support",   Icons.AutoMirrored.Filled.HelpOutline, Route.Feedback.create()),
            DrawerEntry("About Us",         Icons.Filled.Info,                  Route.About.route),
        ),
    ),
)

/* ---------------- Drawer ---------------- */

@Composable
fun AppDrawer(
    onClose: () -> Unit,
    onNavigate: (String) -> Unit,
) {
    val ctx = LocalContext.current

    ModalDrawerSheet(
        modifier = Modifier.fillMaxHeight().width(310.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            /* ----- Hero header: app logo + name + objective ----- */
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .background(NoorGradients.EmeraldDeep)
            ) {
                GeometricPatternBg(modifier = Modifier.fillMaxWidth().height(190.dp))
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    // App logo
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_launcher_background),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(48.dp),
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Noor-e-Islam",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Your daily companion for Quran, Hadith, Salah & Islamic guidance.",
                        color = Gold500,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            /* ----- Sections ----- */
            sections.forEach { section ->
                Spacer(Modifier.height(18.dp))
                SectionHeader(section.title)
                Spacer(Modifier.height(6.dp))
                section.items.forEach { entry ->
                    DrawerRow(
                        entry = entry,
                        onClick = {
                            when (entry.route) {
                                "share_app" -> shareApp(ctx)
                                "help"      -> openHelp(ctx)
                                else        -> onNavigate(entry.route)
                            }
                        }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            /* ----- Footer ----- */
            HorizontalThinDivider()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Version ${BuildConfig.VERSION_NAME}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "Made with ❤ for the Ummah",
                    style = MaterialTheme.typography.labelSmall,
                    color = Gold500,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.height(12.dp))

            // Silence unused-param warning until onClose is wired to a swipe handler.
            @Suppress("UNUSED_EXPRESSION") onClose
        }
    }
}

/* ---------------- Components ---------------- */

@Composable
private fun SectionHeader(title: String) {
    Text(
        title.uppercase(),
        modifier = Modifier.padding(horizontal = 20.dp),
        style = MaterialTheme.typography.labelSmall,
        color = Gold500,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun DrawerRow(entry: DrawerEntry, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Emerald100),
            contentAlignment = Alignment.Center,
        ) {
            Icon(entry.icon, contentDescription = null, tint = Emerald900)
        }
        Spacer(Modifier.width(14.dp))
        Text(
            entry.label,
            style = MaterialTheme.typography.bodyLarge,
            color = Emerald900,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun HorizontalThinDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(1.dp)
            .background(Emerald100)
    )
}

/* ---------------- Side-effects ---------------- */

private fun shareApp(ctx: android.content.Context) {
    val send = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(
            android.content.Intent.EXTRA_TEXT,
            "Try Noor-e-Islam — a beautiful companion for Quran, Hadith, Salah & more.",
        )
    }
    ctx.startActivity(
        android.content.Intent.createChooser(send, "Share Noor-e-Islam")
            .addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
    )
}

private fun openHelp(ctx: android.content.Context) {
    val intent = android.content.Intent(android.content.Intent.ACTION_SENDTO).apply {
        data = android.net.Uri.parse("mailto:support@noor-e-islam.app?subject=Noor-e-Islam%20Support")
        addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    runCatching { ctx.startActivity(intent) }
}
