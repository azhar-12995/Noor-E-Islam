package com.azhar.noor_e_islam.presentation.dua

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.azhar.noor_e_islam.core.ui.components.IslamicCard
import com.azhar.noor_e_islam.ui.theme.Emerald900
import com.azhar.noor_e_islam.ui.theme.Gold500

private data class DuaCategory(val label: String, val icon: ImageVector)

private val categories = listOf(
    DuaCategory("Daily",   Icons.Filled.WbSunny),
    DuaCategory("Morning", Icons.Filled.Brightness4),
    DuaCategory("Evening", Icons.Filled.WbTwilight),
    DuaCategory("Sleep",   Icons.Filled.Bedtime),
    DuaCategory("Others",  Icons.Filled.MoreHoriz),
)

private val duas = listOf(
    "Dua for Entering Home",
    "Dua for Leaving Home",
    "Dua before Sleeping",
    "Dua for Parents",
    "Dua for Forgiveness",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuaScreen(onBack: () -> Unit = {}) {
    var selected by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Dua Collection", color = Emerald900, fontWeight = FontWeight.Bold) },
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
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Category row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                categories.forEachIndexed { idx, cat ->
                    CategoryItem(cat, selected = selected == idx) { selected = idx }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp)
            ) {
                items(duas) { dua ->
                    IslamicCard(modifier = Modifier.fillMaxWidth(), onClick = {}) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.WbSunny,  // prayer-hand placeholder
                                null,
                                tint = Emerald900,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                dua,
                                style = MaterialTheme.typography.titleMedium,
                                color = Emerald900,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                null,
                                tint = Emerald900
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryItem(cat: DuaCategory, selected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    if (selected) Gold500.copy(alpha = 0.30f)
                    else Color.Transparent
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                cat.icon,
                null,
                tint = if (selected) Gold500 else Emerald900,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            cat.label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) Gold500 else Emerald900,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}
