package com.azhar.noor_e_islam.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.outlined.NotificationsNone
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azhar.noor_e_islam.core.ui.components.IslamicCard
import com.azhar.noor_e_islam.domain.model.User
import com.azhar.noor_e_islam.domain.repository.AuthRepository
import com.azhar.noor_e_islam.ui.theme.Emerald900
import com.azhar.noor_e_islam.ui.theme.Gold500
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: AuthRepository,
) : ViewModel() {
    val user = auth.currentUser.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
    fun logout(onDone: () -> Unit) = viewModelScope.launch { auth.logout(); onDone() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onNotes: () -> Unit,
    onBookmarks: () -> Unit,
    onSettings: () -> Unit,
    onProgress: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val user: User? by viewModel.user.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profile", color = Emerald900, fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Emerald900.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.Person,
                            null,
                            tint = Emerald900,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            user?.name?.ifBlank { "Abdullah" } ?: "Abdullah",
                            style = MaterialTheme.typography.titleMedium,
                            color = Emerald900,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            user?.email?.ifBlank { "abdullah123@gmail.com" } ?: "abdullah123@gmail.com",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.Edit, null, tint = Emerald900)
                    }
                }
            }

            val rows = listOf(
                ProfileRow("Reading Progress", Icons.Filled.Timeline,           onProgress),
                ProfileRow("Bookmarks",        Icons.Filled.BookmarkBorder,     onBookmarks),
                ProfileRow("Downloads",        Icons.Filled.Download,           onNotes),
                ProfileRow("Settings",         Icons.Filled.Settings,           onSettings),
                ProfileRow("Help & Support",   Icons.AutoMirrored.Filled.HelpOutline, {}),
            )
            items(rows) { row -> ProfileItem(row) }

            item {
                IslamicCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.logout(onLogout) }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            null,
                            tint = Gold500
                        )
                        Spacer(Modifier.width(14.dp))
                        Text(
                            "Logout",
                            style = MaterialTheme.typography.titleMedium,
                            color = Gold500,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

private data class ProfileRow(val label: String, val icon: ImageVector, val onClick: () -> Unit)

@Composable
private fun ProfileItem(row: ProfileRow) {
    IslamicCard(modifier = Modifier.fillMaxWidth(), onClick = row.onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(row.icon, null, tint = Emerald900)
            Spacer(Modifier.width(14.dp))
            Text(
                row.label,
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
