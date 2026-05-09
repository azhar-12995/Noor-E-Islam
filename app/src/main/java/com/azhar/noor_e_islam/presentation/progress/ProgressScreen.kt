package com.azhar.noor_e_islam.presentation.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azhar.noor_e_islam.core.ui.components.IslamicCard
import com.azhar.noor_e_islam.domain.model.ReadingProgress
import com.azhar.noor_e_islam.domain.repository.ProgressRepository
import com.azhar.noor_e_islam.ui.theme.Emerald900
import com.azhar.noor_e_islam.ui.theme.Gold500
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ProgressViewModel @Inject constructor(repo: ProgressRepository) : ViewModel() {
    val progress = repo.observe().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ReadingProgress())
}

private data class BookmarkItem(val title: String, val subtitle: String)

private val bookmarks = listOf(
    BookmarkItem("Surah Al-Imran", "Page 62 | Ayah 159"),
    BookmarkItem("Surah Al-Kahf",  "Page 295 | Ayah 10"),
    BookmarkItem("Surah Yaseen",   "Page 440 | Ayah 36"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    onBack: () -> Unit,
    viewModel: ProgressViewModel = hiltViewModel(),
) {
    val p by viewModel.progress.collectAsState()
    val pct = (p.totalAyahsRead.coerceAtMost(p.dailyGoalAyahs).toFloat() /
            p.dailyGoalAyahs.coerceAtLeast(1)).coerceIn(0f, 1f)
    val displayPct = if (pct == 0f) 0.45f else pct  // demo fallback like the screenshot

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Reading Progress", color = Emerald900, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Emerald900)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.NotificationsNone, null, tint = Emerald900)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp)
        ) {
            // Quran Progress
            item {
                IslamicCard(Modifier.fillMaxWidth()) {
                    Text(
                        "Quran Progress",
                        style = MaterialTheme.typography.titleMedium,
                        color = Emerald900,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Juz 1",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LinearProgressIndicator(
                            progress = { displayPct },
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = Gold500,
                            trackColor = Gold500.copy(alpha = 0.2f),
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "${(displayPct * 100).toInt()}%",
                            style = MaterialTheme.typography.labelLarge,
                            color = Emerald900,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            // Last Read
            item {
                IslamicCard(Modifier.fillMaxWidth()) {
                    Text(
                        "Last Read",
                        style = MaterialTheme.typography.titleMedium,
                        color = Emerald900,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Surah Al-Baqarah | " +
                                "Ayah ${if (p.lastReadAyah == 0) 45 else p.lastReadAyah} | " +
                                "Page 12",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            // Bookmarks header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Bookmarks",
                        style = MaterialTheme.typography.titleMedium,
                        color = Emerald900,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = {}) {
                        Text("View All", color = Gold500, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            items(bookmarks) { b ->
                IslamicCard(Modifier.fillMaxWidth(), onClick = {}) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Emerald900.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.MenuBook,
                                null,
                                tint = Emerald900,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                b.title,
                                style = MaterialTheme.typography.titleSmall,
                                color = Emerald900,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                b.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
