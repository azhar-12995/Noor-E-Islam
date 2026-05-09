package com.azhar.noor_e_islam.presentation.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azhar.noor_e_islam.core.ui.components.IslamicCard
import com.azhar.noor_e_islam.domain.model.Habit
import com.azhar.noor_e_islam.domain.repository.HabitRepository
import com.azhar.noor_e_islam.ui.theme.Emerald900
import com.azhar.noor_e_islam.ui.theme.Gold500
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HabitsViewModel @Inject constructor(
    private val repo: HabitRepository
) : ViewModel() {
    val habits = repo.observeAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addSeed() = viewModelScope.launch {
        listOf(
            Habit(UUID.randomUUID().toString(), "Sincerity (Ikhlas)", "★", 1),
            Habit(UUID.randomUUID().toString(), "Be Punctual",        "🕐", 1),
            Habit(UUID.randomUUID().toString(), "Speak Good",         "💬", 1),
            Habit(UUID.randomUUID().toString(), "Lower Your Gaze",    "👁", 1),
            Habit(UUID.randomUUID().toString(), "Be Grateful",        "♡", 1),
        ).forEach { repo.add(it) }
    }
    fun log(id: String) = viewModelScope.launch { repo.log(id) }
}

private data class GoodHabit(val title: String, val description: String, val icon: ImageVector)

private val seededHabits = listOf(
    GoodHabit("Sincerity (Ikhlas)", "Do everything for the sake of Allah.",     Icons.Filled.Star),
    GoodHabit("Be Punctual",        "Value time and fulfill commitments.",      Icons.Filled.AccessTime),
    GoodHabit("Speak Good",         "Kind words are a form of charity.",        Icons.Filled.Chat),
    GoodHabit("Lower Your Gaze",    "It protects your heart.",                  Icons.Filled.RemoveRedEye),
    GoodHabit("Be Grateful",        "Gratitude brings more blessings.",         Icons.Filled.Favorite),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsScreen(
    onMenu: () -> Unit = {},
    viewModel: HabitsViewModel = hiltViewModel(),
) {
    val items by viewModel.habits.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Good Habits", color = Emerald900, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onMenu) {
                        Icon(Icons.Outlined.Menu, null, tint = Emerald900)
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
        },
        floatingActionButton = {
            if (items.isEmpty()) {
                FloatingActionButton(onClick = { viewModel.addSeed() }, containerColor = Emerald900) {
                    Icon(Icons.Filled.Add, null, tint = Color.White)
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp)
        ) {
            items(seededHabits) { h ->
                HabitRow(h)
            }
        }
    }
}

@Composable
private fun HabitRow(habit: GoodHabit) {
    IslamicCard(modifier = Modifier.fillMaxWidth(), onClick = {}) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Emerald900),
                contentAlignment = Alignment.Center
            ) {
                Icon(habit.icon, null, tint = Gold500, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    habit.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Emerald900,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    habit.description,
                    style = MaterialTheme.typography.bodySmall,
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
