package com.azhar.noor_e_islam.presentation.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.azhar.noor_e_islam.core.ui.components.IslamicCard
import com.azhar.noor_e_islam.ui.theme.Emerald100
import com.azhar.noor_e_islam.ui.theme.Emerald700
import com.azhar.noor_e_islam.ui.theme.Emerald900
import com.azhar.noor_e_islam.ui.theme.Gold500
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onBack: () -> Unit = {},
    onNavigate: (NotifTarget) -> Unit = {},
    viewModel: NotificationsViewModel = hiltViewModel(),
) {
    val state by viewModel.ui.collectAsStateWithLifecycle()
    var detail: NotifItem? by remember { mutableStateOf(null) }

    detail?.let { item ->
        NotifDetailDialog(item, onDismiss = { detail = null })
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Notifications", color = Emerald900, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Emerald900)
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::refresh) {
                        Icon(Icons.Filled.Refresh, null, tint = Emerald900)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        }
    ) { padding ->
        when {
            state.isLoading && state.items.isEmpty() ->
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Emerald900)
                }
            state.items.isEmpty() ->
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text(
                        "No notifications right now.\nWe'll let you know when something happens.",
                        color = Emerald700,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            else -> {
                // Group by category, ordered with the most actionable categories first.
                val order = listOf(
                    NotifCategory.ANNOUNCEMENT,
                    NotifCategory.FEEDBACK,
                    NotifCategory.PRAYER,
                    NotifCategory.EVENT,
                    NotifCategory.HADITH,
                )
                val grouped = state.items.groupBy { it.category }
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    order.forEach { cat ->
                        val list = grouped[cat].orEmpty()
                        if (list.isEmpty()) return@forEach
                        item(key = "header-$cat") { SectionHeader(cat, list.size) }
                        items(list, key = { it.id }) { item ->
                            NotifCard(item, onClick = {
                                // Announcement details open inline as a premium
                                // dialog; everything else (hadith / event /
                                // prayer / feedback) navigates to its screen.
                                if (item.category == NotifCategory.ANNOUNCEMENT) {
                                    detail = item
                                } else {
                                    onNavigate(item.target)
                                }
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(category: NotifCategory, count: Int) {
    val (_, tint) = visualsFor(category)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp),
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(tint),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            categoryLabel(category).uppercase(Locale.getDefault()),
            style = MaterialTheme.typography.labelLarge,
            color = Emerald900,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.width(8.dp))
        Text(
            "($count)",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun NotifCard(item: NotifItem, onClick: () -> Unit) {
    val (icon, tint) = visualsFor(item.category)
    IslamicCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = if (item.target is NotifTarget.None) null else onClick,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = tint)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        item.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = Emerald900,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Emerald100)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            categoryLabel(item.category),
                            style = MaterialTheme.typography.labelSmall,
                            color = Emerald900,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
                if (item.message.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        item.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                    )
                }
                if (item.timestamp > 0L) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        SimpleDateFormat("MMM d, hh:mm a", Locale.getDefault()).format(Date(item.timestamp)),
                        style = MaterialTheme.typography.labelSmall,
                        color = Gold500,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

private fun visualsFor(c: NotifCategory): Pair<ImageVector, Color> = when (c) {
    NotifCategory.PRAYER       -> Icons.Filled.Mosque         to Color(0xFF0F4D38)
    NotifCategory.HADITH       -> Icons.AutoMirrored.Filled.MenuBook to Color(0xFFD4AF37)
    NotifCategory.EVENT        -> Icons.Filled.EventNote      to Color(0xFF345C99)
    NotifCategory.ANNOUNCEMENT -> Icons.Filled.Campaign       to Color(0xFF8C2A2A)
    NotifCategory.FEEDBACK     -> Icons.Filled.Feedback       to Color(0xFF5B3F8C)
}

private fun categoryLabel(c: NotifCategory): String = when (c) {
    NotifCategory.PRAYER       -> "Prayer"
    NotifCategory.HADITH       -> "Hadith"
    NotifCategory.EVENT        -> "Event"
    NotifCategory.ANNOUNCEMENT -> "News"
    NotifCategory.FEEDBACK     -> "Feedback"
}

/* -------- Inline detail dialog (announcements) -------- */
@Composable
private fun NotifDetailDialog(item: NotifItem, onDismiss: () -> Unit) {
    val (icon, tint) = visualsFor(item.category)
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(24.dp))
                .background(com.azhar.noor_e_islam.ui.theme.NoorGradients.EmeraldDeep)
                .padding(1.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(tint.copy(alpha = 0.28f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(icon, null, tint = Gold500)
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        categoryLabel(item.category),
                        style = MaterialTheme.typography.labelLarge,
                        color = Gold500,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    item.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
                if (item.message.isNotBlank()) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        item.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f),
                    )
                }
                if (item.timestamp > 0L) {
                    Spacer(Modifier.height(14.dp))
                    Text(
                        java.text.SimpleDateFormat("MMM d, yyyy — hh:mm a", Locale.getDefault())
                            .format(Date(item.timestamp)),
                        style = MaterialTheme.typography.labelSmall,
                        color = Gold500,
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
                    ) { Text("Close", fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}
