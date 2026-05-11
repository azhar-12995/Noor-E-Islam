package com.azhar.noor_e_islam.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azhar.noor_e_islam.core.ui.components.IslamicCard
import com.azhar.noor_e_islam.domain.repository.UserPrefsRepository
import com.azhar.noor_e_islam.ui.theme.Emerald900
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repo: UserPrefsRepository,
) : ViewModel() {
    val prefs = repo.prefs
    fun setTheme(mode: String) = viewModelScope.launch { repo.setThemeMode(mode) }
    fun setLocale(loc: String) = viewModelScope.launch { repo.setLocale(loc) }
    fun setQuranFont(scale: Float) = viewModelScope.launch { repo.setQuranFontScale(scale) }
    fun setHadithFont(scale: Float) = viewModelScope.launch { repo.setHadithFontScale(scale) }
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
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            IslamicCard(Modifier.fillMaxWidth()) {
                Text("Theme: ${prefs?.themeMode ?: "system"}", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("system", "light", "dark").forEach {
                        FilterChip(
                            selected = prefs?.themeMode == it,
                            onClick = { viewModel.setTheme(it) },
                            label = { Text(it) },
                        )
                    }
                }
            }
            IslamicCard(Modifier.fillMaxWidth()) {
                Text("Language: ${prefs?.locale ?: "en"}", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("en", "ur", "ar").forEach {
                        FilterChip(
                            selected = prefs?.locale == it,
                            onClick = { viewModel.setLocale(it) },
                            label = { Text(it) },
                        )
                    }
                }
            }

            /* ---- Quran font size ---- */
            IslamicCard(Modifier.fillMaxWidth()) {
                val q = prefs?.quranFontScale ?: 1f
                Text("Qurʾān text size", style = MaterialTheme.typography.titleMedium, color = Emerald900, fontWeight = FontWeight.Bold)
                Text(
                    "Preview: " + percentLabel(q),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(4.dp))
                Slider(
                    value = q,
                    onValueChange = { viewModel.setQuranFont(it) },
                    valueRange = 0.8f..2.0f,
                    steps = 11,
                )
                Text(
                    "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = MaterialTheme.typography.headlineSmall.fontSize * q
                    ),
                    color = Emerald900,
                )
            }

            /* ---- Hadith font size ---- */
            IslamicCard(Modifier.fillMaxWidth()) {
                val h = prefs?.hadithFontScale ?: 1f
                Text("Hadith text size", style = MaterialTheme.typography.titleMedium, color = Emerald900, fontWeight = FontWeight.Bold)
                Text(
                    "Preview: " + percentLabel(h),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(4.dp))
                Slider(
                    value = h,
                    onValueChange = { viewModel.setHadithFont(it) },
                    valueRange = 0.8f..2.0f,
                    steps = 11,
                )
                Text(
                    "The Prophet ﷺ said: \"Actions are but by intentions…\"",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize * h
                    ),
                    color = Emerald900,
                )
            }

            IslamicCard(Modifier.fillMaxWidth()) {
                Text("Notifications", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Daily hadith, prayer & habit reminders.",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            IslamicCard(Modifier.fillMaxWidth()) {
                Text("Backup & Restore (Firebase)", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Cloud-synced bookmarks and notes.",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

private fun percentLabel(scale: Float): String = "${(scale * 100).toInt()}%"
