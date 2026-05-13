package com.azhar.noor_e_islam.presentation.learn

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.azhar.noor_e_islam.core.ui.components.IslamicCard
import com.azhar.noor_e_islam.ui.theme.Emerald900
import com.azhar.noor_e_islam.ui.theme.Gold500

private data class LearnTopic(val title: String, val icon: ImageVector, val key: String)

private val topics = listOf(
    LearnTopic("Pillars of Islam",   Icons.Filled.Mosque,                     "pillars"),
    LearnTopic("Kalmas",             Icons.Filled.FormatQuote,                "kalmas"),
    LearnTopic("Namaz",              Icons.Filled.SelfImprovement,            "namaz"),
    LearnTopic("Wazu",               Icons.Filled.WaterDrop,                  "wazu"),
    LearnTopic("Iman (Beliefs)",     Icons.Filled.Favorite,                   "iman"),
    LearnTopic("Seerah of Prophet ﷺ", Icons.Filled.People,                    "seerah"),
    LearnTopic("History of Islam",   Icons.Filled.History,                    "history"),
    LearnTopic("Akhlaq (Character)", Icons.Filled.Bolt,                       "akhlaq"),
    LearnTopic("Fiqh (Basics)",      Icons.AutoMirrored.Filled.MenuBook,      "fiqh"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnScreen(
    onBack: () -> Unit = {},
    onOpenPillars: () -> Unit = {},
    onOpenKalmas: () -> Unit = {},
    onOpenNamaz: () -> Unit = {},
    onOpenWazu: () -> Unit = {},
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Learn Islam", color = Emerald900, fontWeight = FontWeight.Bold) },
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
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp)
        ) {
            items(topics) { topic ->
                IslamicCard(
                    modifier = Modifier.fillMaxWidth().height(140.dp),
                    onClick = {
                        when (topic.key) {
                            "pillars" -> onOpenPillars()
                            "kalmas"  -> onOpenKalmas()
                            "namaz"   -> onOpenNamaz()
                            "wazu"    -> onOpenWazu()
                            else      -> Unit
                        }
                    }
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Gold500.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(topic.icon, null, tint = Emerald900, modifier = Modifier.size(26.dp))
                        }
                        Spacer(Modifier.height(10.dp))
                        Text(
                            topic.title,
                            style = MaterialTheme.typography.labelLarge,
                            color = Emerald900,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
