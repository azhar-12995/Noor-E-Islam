package com.azhar.noor_e_islam.presentation.feedback

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.azhar.noor_e_islam.core.ui.components.GoldButton
import com.azhar.noor_e_islam.core.ui.components.IslamicCard
import com.azhar.noor_e_islam.core.util.ImageBase64
import com.azhar.noor_e_islam.data.remote.firebase.FeedbackFirestoreService
import com.azhar.noor_e_islam.ui.theme.Emerald100
import com.azhar.noor_e_islam.ui.theme.Emerald50
import com.azhar.noor_e_islam.ui.theme.Emerald900
import com.azhar.noor_e_islam.ui.theme.Gold500
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(
    onBack: () -> Unit = {},
    highlightId: String? = null,
    viewModel: FeedbackViewModel = hiltViewModel(),
) {
    val state by viewModel.ui.collectAsStateWithLifecycle()
    val ctx = LocalContext.current
    val scope = remember { MainScope() }
    val listState = rememberLazyListState()

    val pick = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri: Uri? ->
        if (uri != null) scope.launch {
            val b64 = withContext(Dispatchers.IO) { ImageBase64.encodeFromUri(ctx, uri) }
            if (!b64.isNullOrBlank()) viewModel.addImage(b64)
        }
    }

    // When opened with a highlight id (e.g. from a feedback-reply notification),
    // scroll to that card once the history loads.
    LaunchedEffect(highlightId, state.history) {
        val id = highlightId ?: return@LaunchedEffect
        val index = state.history.indexOfFirst { it.id == id }
        if (index >= 0) {
            // +2 accounts for the compose card and the "Your Feedback" header items.
            listState.animateScrollToItem(index + 2)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Feedback & Suggestions", color = Emerald900, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Emerald900)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
        ) {
            item { ComposeCard(state, viewModel, onPickImage = {
                pick.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }) }

            item {
                Text(
                    "Your Feedback",
                    style = MaterialTheme.typography.titleMedium,
                    color = Emerald900,
                    fontWeight = FontWeight.Bold,
                )
            }

            if (state.isHistoryLoading) {
                item {
                    Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Emerald900)
                    }
                }
            } else if (state.history.isEmpty()) {
                item {
                    IslamicCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "You haven't submitted any feedback yet.",
                            color = Emerald900,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            } else {
                items(state.history, key = { it.id }) { HistoryCard(it, isHighlighted = it.id == highlightId) }
            }
        }
    }
}

@Composable
private fun ComposeCard(
    state: FeedbackUiState,
    vm: FeedbackViewModel,
    onPickImage: () -> Unit,
) {
    IslamicCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            "What can we improve?",
            style = MaterialTheme.typography.titleSmall,
            color = Emerald900,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = state.text,
            onValueChange = vm::setText,
            placeholder = { Text("Describe a bug, a suggestion, or share what you love.") },
            minLines = 4,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(10.dp))

        if (state.images.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                itemsIndexed(state.images) { index, b64 ->
                    val bmp = remember(b64) { ImageBase64.decodeToBitmap(b64) }
                    Box(modifier = Modifier.size(80.dp)) {
                        if (bmp != null) {
                            Image(
                                bitmap = bmp.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(10.dp)),
                            )
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f))
                                .clickable { vm.removeImage(index) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Filled.Close, null, tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(onClick = onPickImage) {
                Icon(Icons.Filled.AddPhotoAlternate, null)
                Spacer(Modifier.width(6.dp))
                Text("Attach image")
            }
            Spacer(Modifier.weight(1f))
            if (state.isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.size(28.dp), color = Emerald900)
            }
        }

        state.error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        state.message?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = Emerald900, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(12.dp))
        GoldButton(
            text = if (state.isSubmitting) "Submitting…" else "Submit Feedback",
            onClick = vm::submit,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun HistoryCard(entry: FeedbackFirestoreService.FeedbackEntry, isHighlighted: Boolean = false) {
    IslamicCard(
        modifier = if (isHighlighted) {
            Modifier
                .fillMaxWidth()
                .border(2.dp, Gold500, RoundedCornerShape(20.dp))
        } else Modifier.fillMaxWidth(),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (entry.isResponded) Emerald100 else Gold500.copy(alpha = 0.18f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (entry.isResponded) Icons.Filled.MarkEmailRead else Icons.Filled.HourglassEmpty,
                        null,
                        tint = if (entry.isResponded) Emerald900 else Gold500,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        if (entry.isResponded) "Responded" else "Pending",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (entry.isResponded) Emerald900 else Gold500,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            Text(
                SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(entry.createdAt)),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.height(8.dp))
        if (entry.text.isNotBlank()) {
            Text(
                entry.text,
                style = MaterialTheme.typography.bodyMedium,
                color = Emerald900,
            )
        }
        if (entry.images.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(entry.images) { b64 ->
                    val bmp = remember(b64) { ImageBase64.decodeToBitmap(b64) }
                    if (bmp != null) {
                        Image(
                            bitmap = bmp.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(10.dp)),
                        )
                    }
                }
            }
        }
        if (!entry.adminResponse.isNullOrBlank()) {
            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Emerald50)
                    .padding(12.dp),
            ) {
                Column {
                    Text(
                        "Response from Noor-e-Islam team",
                        style = MaterialTheme.typography.labelSmall,
                        color = Gold500,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        entry.adminResponse,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Emerald900,
                    )
                }
            }
        }
    }
}

