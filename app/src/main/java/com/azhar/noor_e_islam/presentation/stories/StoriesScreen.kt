package com.azhar.noor_e_islam.presentation.stories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.azhar.noor_e_islam.core.ui.components.IslamicCard
import com.azhar.noor_e_islam.ui.theme.Emerald900
import com.azhar.noor_e_islam.ui.theme.Gold500

private data class Story(val title: String, val date: String, val emoji: String)

private val ghazwat = listOf(
    Story("Ghazwa Badr",    "2 AH", "⚔"),
    Story("Ghazwa Uhud",    "3 AH", "⚔"),
    Story("Ghazwa Khandaq", "5 AH", "⚔"),
    Story("Ghazwa Hunayn",  "8 AH", "⚔"),
)
private val sahaba = listOf(
    Story("Abu Bakr al-Siddiq",  "RA", "★"),
    Story("Umar ibn al-Khattab", "RA", "★"),
    Story("Uthman ibn Affan",    "RA", "★"),
    Story("Ali ibn Abi Talib",   "RA", "★"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoriesScreen(onBack: () -> Unit = {}) {
    var tab by remember { mutableIntStateOf(0) }
    val items = if (tab == 0) ghazwat else sahaba

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Stories", color = Emerald900, fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tab pill row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("Ghazwat", "Sahaba Ikram").forEachIndexed { idx, label ->
                    val selected = tab == idx
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selected) Emerald900 else androidx.compose.ui.graphics.Color.Transparent)
                            .clickable { tab = idx }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            label,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (selected) androidx.compose.ui.graphics.Color.White else Emerald900,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp)
            ) {
                items(items) { story ->
                    StoryRow(story)
                }
            }
        }
    }
}

@Composable
private fun StoryRow(story: Story) {
    IslamicCard(modifier = Modifier.fillMaxWidth(), onClick = {}) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Gold500.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    story.emoji,
                    style = MaterialTheme.typography.titleLarge,
                    color = Gold500
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    story.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Emerald900,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    story.date,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                null,
                tint = Emerald900
            )
        }
    }
}

// shorthand for stable clickable on Box — uses foundation.clickable imported above
