package com.azhar.noor_e_islam.presentation.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azhar.noor_e_islam.core.ui.components.EmptyState
import com.azhar.noor_e_islam.core.ui.components.IslamicCard
import com.azhar.noor_e_islam.domain.model.Note
import com.azhar.noor_e_islam.domain.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val repo: NoteRepository
) : ViewModel() {
    val notes = repo.observeAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    fun delete(id: String) = viewModelScope.launch { repo.delete(id) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    onAdd: () -> Unit,
    onEdit: (String) -> Unit,
    viewModel: NotesViewModel = hiltViewModel(),
) {
    val items by viewModel.notes.collectAsState()
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd) { Icon(Icons.Filled.Add, null) }
        }
    ) { padding ->
        if (items.isEmpty()) EmptyState(message = "No notes — tap + to add", modifier = Modifier.padding(padding))
        else LazyColumn(
            modifier = Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items, key = { it.id }) { n ->
                IslamicCard(modifier = Modifier.fillMaxWidth(), onClick = { onEdit(n.id) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(n.title, style = MaterialTheme.typography.titleMedium)
                            Text(n.body, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                        }
                        IconButton(onClick = { viewModel.delete(n.id) }) {
                            Icon(Icons.Filled.Delete, null, tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@HiltViewModel
class NoteEditorViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val repo: NoteRepository,
) : ViewModel() {
    private val noteId: String? = handle.get<String>("id")?.takeIf { it.isNotBlank() }
    val title = MutableStateFlow("")
    val body  = MutableStateFlow("")

    init {
        viewModelScope.launch {
            noteId?.let {
                val existing = repo.get(it)
                if (existing != null) { title.value = existing.title; body.value = existing.body }
            }
        }
    }

    fun save(onDone: () -> Unit) = viewModelScope.launch {
        val now = System.currentTimeMillis()
        repo.upsert(Note(noteId ?: UUID.randomUUID().toString(), title.value, body.value, emptyList(), now, now))
        onDone()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    noteId: String?,
    onBack: () -> Unit,
    viewModel: NoteEditorViewModel = hiltViewModel(),
) {
    val title by viewModel.title.collectAsState()
    val body  by viewModel.body.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (noteId == null) "New Note" else "Edit Note") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
                actions = { TextButton(onClick = { viewModel.save(onBack) }) { Text("Save") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = title, onValueChange = { viewModel.title.value = it }, label = { Text("Title") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = body, onValueChange = { viewModel.body.value = it }, label = { Text("Body") }, modifier = Modifier.fillMaxWidth().weight(1f))
        }
    }
}

