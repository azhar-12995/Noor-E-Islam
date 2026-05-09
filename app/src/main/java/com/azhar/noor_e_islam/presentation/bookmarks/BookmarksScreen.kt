package com.azhar.noor_e_islam.presentation.bookmarks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azhar.noor_e_islam.core.ui.components.EmptyState
import com.azhar.noor_e_islam.core.ui.components.IslamicCard
import com.azhar.noor_e_islam.core.ui.components.SectionHeader
import com.azhar.noor_e_islam.domain.repository.BookmarkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(repo: BookmarkRepository) : ViewModel() {
    val items = repo.observeAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

@Composable
fun BookmarksScreen(viewModel: BookmarksViewModel = hiltViewModel()) {
    val items by viewModel.items.collectAsState()
    Column(modifier = Modifier.fillMaxSize().padding(top = 16.dp, bottom = 88.dp)) {
        SectionHeader(title = "Bookmarks")
        if (items.isEmpty()) EmptyState(message = "No bookmarks yet")
        else LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items, key = { it.id }) { b ->
                IslamicCard(modifier = Modifier.fillMaxWidth()) {
                    Text(b.title, style = MaterialTheme.typography.titleMedium)
                    b.subtitle?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline) }
                }
            }
        }
    }
}

