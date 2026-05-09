package com.azhar.noor_e_islam.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azhar.noor_e_islam.core.ui.components.IslamicCard
import com.azhar.noor_e_islam.domain.repository.UserPrefsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repo: UserPrefsRepository
) : ViewModel() {
    val prefs = repo.prefs
    fun setTheme(mode: String) = viewModelScope.launch { repo.setThemeMode(mode) }
    fun setLocale(loc: String) = viewModelScope.launch { repo.setLocale(loc) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val prefs by viewModel.prefs.collectAsState(initial = null)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            IslamicCard(Modifier.fillMaxWidth()) {
                Text("Theme: ${prefs?.themeMode ?: "system"}", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("system","light","dark").forEach {
                        FilterChip(selected = prefs?.themeMode == it, onClick = { viewModel.setTheme(it) }, label = { Text(it) })
                    }
                }
            }
            IslamicCard(Modifier.fillMaxWidth()) {
                Text("Language: ${prefs?.locale ?: "en"}", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("en","ur","ar").forEach {
                        FilterChip(selected = prefs?.locale == it, onClick = { viewModel.setLocale(it) }, label = { Text(it) })
                    }
                }
            }
            IslamicCard(Modifier.fillMaxWidth()) {
                Text("Notifications", style = MaterialTheme.typography.titleMedium)
                Text("Daily hadith, prayer & habit reminders.", style = MaterialTheme.typography.bodySmall)
            }
            IslamicCard(Modifier.fillMaxWidth()) {
                Text("Backup & Restore (Firebase)", style = MaterialTheme.typography.titleMedium)
                Text("Cloud-synced bookmarks and notes.", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

