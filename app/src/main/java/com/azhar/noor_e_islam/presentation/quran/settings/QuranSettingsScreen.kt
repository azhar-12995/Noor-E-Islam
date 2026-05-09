package com.azhar.noor_e_islam.presentation.quran.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.azhar.noor_e_islam.core.datastore.UserPreferences
import com.azhar.noor_e_islam.core.ui.components.IslamicCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuranSettingsViewModel @Inject constructor(
    private val store: UserPreferences
) : ViewModel() {
    val prefs = store.prefs
    fun setFontScale(v: Float) = viewModelScope.launch { store.setFontScale(v) }
    fun setTranslation(v: String) = viewModelScope.launch { store.setTranslation(v) }
    fun setReciter(v: String) = viewModelScope.launch { store.setReciter(v) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranSettingsScreen(
    onBack: () -> Unit,
    viewModel: QuranSettingsViewModel = hiltViewModel(),
) {
    val prefs by viewModel.prefs.collectAsState(initial = null)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quran Settings") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IslamicCard(modifier = Modifier.fillMaxWidth()) {
                Text("Font size", style = MaterialTheme.typography.titleMedium)
                Slider(
                    value = prefs?.fontScale ?: 1f,
                    onValueChange = viewModel::setFontScale,
                    valueRange = 0.8f..1.6f, steps = 7
                )
            }
            IslamicCard(modifier = Modifier.fillMaxWidth()) {
                Text("Translation: ${prefs?.translation ?: "—"}", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                FilterChip(selected = prefs?.translation == "en.sahih", onClick = { viewModel.setTranslation("en.sahih") }, label = { Text("English (Sahih)") })
                FilterChip(selected = prefs?.translation == "ur.junagarhi", onClick = { viewModel.setTranslation("ur.junagarhi") }, label = { Text("Urdu (Junagarhi)") })
            }
            IslamicCard(modifier = Modifier.fillMaxWidth()) {
                Text("Reciter: ${prefs?.reciter ?: "—"}", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                FilterChip(selected = prefs?.reciter == "ar.alafasy", onClick = { viewModel.setReciter("ar.alafasy") }, label = { Text("Mishary Alafasy") })
                FilterChip(selected = prefs?.reciter == "ar.husary", onClick = { viewModel.setReciter("ar.husary") }, label = { Text("Mahmoud al-Husary") })
            }
        }
    }
}

