package com.azhar.noor_e_islam.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.azhar.noor_e_islam.core.ui.components.BadgedBell
import com.azhar.noor_e_islam.core.ui.components.IslamicCard
import com.azhar.noor_e_islam.presentation.prayertimes.PrayerTimesHomeCard
import com.azhar.noor_e_islam.presentation.qibla.QiblaHomeCard
import com.azhar.noor_e_islam.ui.theme.Emerald900
import com.azhar.noor_e_islam.ui.theme.Gold500

private data class QuickItem(val label: String, val icon: ImageVector, val onClick: () -> Unit)
private data class HighlightItem(val tag: String, val text: String, val ref: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenSurah: (Int) -> Unit,
    onOpenQuran: () -> Unit,
    onOpenCalendar: () -> Unit,
    onOpenDua: () -> Unit,
    onOpenStories: () -> Unit,
    onOpenLearn: () -> Unit,
    onOpenMenu: () -> Unit = {},
    onOpenHadith: () -> Unit = {},
    onOpenQibla: () -> Unit = {},
    onOpenPrayerTimes: () -> Unit = {},
    onOpenIncidents: () -> Unit = {},
    onOpenHabits: () -> Unit = {},
    onOpenBookmarks: () -> Unit = {},
    onOpenNotes: () -> Unit = {},
    onOpenNotifications: () -> Unit = {},
    onOpenFeedback: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    // Premium announcement dialog — shown at most once per announcement id.
    state.latestAnnouncement?.let { ann ->
        AnnouncementDialog(
            title = ann.title,
            message = ann.message,
            onDismiss = { viewModel.dismissAnnouncement(ann.id) },
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            // Fixed (non-scrolling) top bar with menu, greeting, notifications.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onOpenMenu) {
                    Icon(Icons.Outlined.Menu, contentDescription = "Menu", tint = Emerald900)
                }
                Spacer(Modifier.width(4.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Assalam o Alaikum",
                        style = MaterialTheme.typography.titleLarge,
                        color = Emerald900,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "May Allah bless your day",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onOpenNotifications) {
                    BadgedBell()
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 110.dp) // clear the global rounded bottom nav
        ) {
            // Sync banner
            if (state.isQuranSyncing) {
                SyncBanner(text = "Downloading Quran… first-time sync")
            } else if (state.quranSyncError != null) {
                SyncBanner(text = "Quran sync failed: ${state.quranSyncError}", isError = true)
            }


        // Qibla compass card (sits directly below the top bar / status bar)
        QiblaHomeCard(
            onOpenFull = onOpenQibla,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        )

        Spacer(Modifier.height(8.dp))

        // Prayer Times card — auto-detects region & timezone
        PrayerTimesHomeCard(
            onOpenFull = onOpenPrayerTimes,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        )

        Spacer(Modifier.height(12.dp))

        // Today's Date card
        IslamicCard(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
        ) {
            Text(
                "Today's Date",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(6.dp))
            Text(
                state.hijri.ifBlank { "Loading…" },
                style = MaterialTheme.typography.titleMedium,
                color = Emerald900,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                state.gregorian,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(20.dp))

        // Today's Highlights
        SectionTitle("Today's Highlights", showArrow = true)
        Spacer(Modifier.height(8.dp))
        val highlights = listOf(
            HighlightItem("Hadith",     "Never belittle a good deed.",            "Sahih Muslim 2626"),
            HighlightItem("Importance", "The day of Dhul-Qi'dah is a virtuous day.", "Tafsir Ibn Kathir"),
            HighlightItem("Incident",   "The Treaty of Hudaybiyyah",              "6 AH"),
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(items = highlights, key = { it.tag }) { item ->
                HighlightCard(item, onClick = if (item.tag == "Hadith") onOpenHadith else ({}))
            }
        }

        Spacer(Modifier.height(20.dp))

        // ===== Quick Access — features NOT in the bottom nav =====
        // Bottom nav already covers: Home, Quran, Learn, Calendar, Profile.
        // Quick Access surfaces: Hadith, Dua, Stories, Incidents, Habits,
        //                        Bookmarks, Notes, Feedback.
        SectionTitle("Quick Access")
        Spacer(Modifier.height(10.dp))
        val quickItems = listOf(
            QuickItem("Hadith",    Icons.Filled.FormatQuote,                onOpenHadith),
            QuickItem("Dua",       Icons.Filled.Favorite,                   onOpenDua),
            QuickItem("Stories",   Icons.Filled.AutoStories,                onOpenStories),
            QuickItem("Incidents", Icons.Filled.History,                    onOpenIncidents),
            QuickItem("Habits",    Icons.Filled.EmojiEvents,                onOpenHabits),
            QuickItem("Bookmarks", Icons.Filled.Bookmark,                   onOpenBookmarks),
            QuickItem("Notes",     Icons.AutoMirrored.Filled.Note,          onOpenNotes),
            QuickItem("Feedback",  Icons.Filled.Feedback,                   onOpenFeedback),
        )
        // 2 rows × 4 columns
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)) {
            quickItems.chunked(4).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    row.forEach { item ->
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            QuickAccessIcon(item)
                        }
                    }
                    repeat(4 - row.size) { Box(modifier = Modifier.weight(1f)) {} }
                }
                Spacer(Modifier.height(8.dp))
            }
        }

        Spacer(Modifier.height(12.dp))

        // Continue Reading
        SectionTitle("Continue Reading")
        Spacer(Modifier.height(8.dp))
        IslamicCard(
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
            onClick = { onOpenSurah(state.lastReadSurah) }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Emerald900),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.MenuBook,
                        null,
                        tint = Gold500,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Surah Al-Baqarah",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Page 12 | Ayah ${state.lastReadAyah}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Gold500.copy(alpha = 0.18f))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        "View",
                        style = MaterialTheme.typography.labelLarge,
                        color = Gold500,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionTitle(text: String, showArrow: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text,
            style = MaterialTheme.typography.titleMedium,
            color = Emerald900,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        if (showArrow) {
            Text("›", color = Emerald900, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun HighlightCard(item: HighlightItem, onClick: () -> Unit) {
    IslamicCard(
        modifier = Modifier.width(180.dp),
        onClick = onClick
    ) {
        Text(item.tag, style = MaterialTheme.typography.labelMedium, color = Gold500, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Text(item.text, style = MaterialTheme.typography.bodyMedium, color = Emerald900, fontWeight = FontWeight.Medium, maxLines = 3)
        Spacer(Modifier.height(10.dp))
        Text(item.ref, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun QuickAccessIcon(item: QuickItem) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = item.onClick)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Gold500.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(item.icon, null, tint = Emerald900, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.height(6.dp))
        Text(
            item.label,
            style = MaterialTheme.typography.labelMedium,
            color = Emerald900,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SyncBanner(text: String, isError: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (isError) MaterialTheme.colorScheme.errorContainer
                else MaterialTheme.colorScheme.primaryContainer
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!isError) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(10.dp))
        }
        Text(
            text,
            style = MaterialTheme.typography.labelMedium,
            color = if (isError) MaterialTheme.colorScheme.onErrorContainer
                    else MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

/* -------- Premium announcement dialog -------- */
@Composable
private fun AnnouncementDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(com.azhar.noor_e_islam.ui.theme.NoorGradients.EmeraldDeep)
                .padding(1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Gold500.copy(alpha = 0.22f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Filled.Campaign, null, tint = Gold500)
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Announcement",
                        style = MaterialTheme.typography.labelLarge,
                        color = Gold500,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge,
                    color = androidx.compose.ui.graphics.Color.White,
                    fontWeight = FontWeight.Bold,
                )
                if (message.isNotBlank()) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.9f),
                    )
                }
                Spacer(Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(contentColor = Gold500),
                    ) {
                        Text("Got it", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
