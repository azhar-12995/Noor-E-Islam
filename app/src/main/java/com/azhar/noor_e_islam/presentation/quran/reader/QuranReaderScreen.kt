package com.azhar.noor_e_islam.presentation.quran.reader

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.azhar.noor_e_islam.core.ui.components.AyahCard
import com.azhar.noor_e_islam.core.ui.components.ErrorState
import com.azhar.noor_e_islam.core.ui.components.LoadingState
import com.azhar.noor_e_islam.domain.model.Ayah
import com.azhar.noor_e_islam.ui.theme.Emerald900
import com.azhar.noor_e_islam.ui.theme.Gold500

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranReaderScreen(
    surahId: Int,
    onBack: () -> Unit,
    onSettings: () -> Unit = {},
    onShareAyah: (Int) -> Unit,
    viewModel: QuranReaderViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val bookmarked by viewModel.bookmarkedRefs.collectAsState()
    val currentAudio by viewModel.currentAudioUrl.collectAsState()
    val isPlaying by viewModel.isAudioPlaying.collectAsState()
    val isPlayingAll by viewModel.isPlayingAll.collectAsState()
    val playingIndex by viewModel.currentPlayingIndex.collectAsState()
    val fontScale by viewModel.fontScale.collectAsState()
    val ctx = LocalContext.current
    val listState = rememberLazyListState()

    // Auto-scroll to the playing ayah in Play-All mode.
    LaunchedEffect(playingIndex) {
        playingIndex?.let { listState.animateScrollToItem(it) }
    }

    // Scroll to the ayah passed in via the route arg (Bookmarks → Reader).
    LaunchedEffect(state.ayahs.size, viewModel.initialAyahNumber) {
        val target = viewModel.initialAyahNumber ?: return@LaunchedEffect
        val idx = state.ayahs.indexOfFirst { it.number == target }
        if (idx >= 0) listState.animateScrollToItem(idx)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Surah ${state.surahId}") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
                actions = {
                    // Play-All / Stop-All toggle button
                    PlayAllAction(
                        isPlayingAll = isPlayingAll,
                        onStart = { viewModel.playAll(0) },
                        onStop  = { viewModel.stopPlayAll() },
                    )
                }
            )
        }
    ) { padding ->
        when {
            state.isLoading      -> LoadingState(modifier = Modifier.padding(padding))
            state.error != null  -> ErrorState(message = state.error!!, modifier = Modifier.padding(padding))
            else -> LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(state.ayahs, key = { "${it.surahNumber}-${it.number}" }) { ayah ->
                    val refId = "${ayah.surahNumber}:${ayah.number}"
                    val effectiveAudio = ayah.effectiveAudioUrl
                    val isThisPlaying = (isPlaying && currentAudio == effectiveAudio && effectiveAudio != null) ||
                        (isPlayingAll && state.ayahs.getOrNull(playingIndex ?: -1)?.number == ayah.number)
                    AyahCard(
                        arabic = ayah.arabic,
                        translation = ayah.translation.ifBlank { "" },
                        reference = "${ayah.surahNumber}:${ayah.number}",
                        fontScale = fontScale,
                        isPlaying = isThisPlaying,
                        isBookmarked = bookmarked.contains(refId),
                        onPlay = { viewModel.togglePlay(ayah); viewModel.markRead(ayah.number) },
                        onBookmark = { viewModel.toggleBookmark(ayah) },
                        onCopy = { copyAyah(ctx, ayah) },
                        onShare = { shareAyah(ctx, ayah); onShareAyah(ayah.number) },
                    )
                }
            }
        }
    }
}

@Composable
private fun PlayAllAction(
    isPlayingAll: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit,
) {
    val brush = if (isPlayingAll) {
        androidx.compose.ui.graphics.Brush.horizontalGradient(
            listOf(Emerald900, com.azhar.noor_e_islam.ui.theme.Emerald700)
        )
    } else {
        androidx.compose.ui.graphics.Brush.horizontalGradient(
            listOf(Gold500, com.azhar.noor_e_islam.ui.theme.Gold700)
        )
    }
    val contentColor = androidx.compose.ui.graphics.Color.White

    Row(
        modifier = Modifier
            .padding(end = 8.dp)
            .clip(RoundedCornerShape(50))
            .background(brush)
            .clickable(onClick = if (isPlayingAll) onStop else onStart)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        // Pulsing dot when playing-all is active
        if (isPlayingAll) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(contentColor),
            )
            Spacer(Modifier.width(6.dp))
        }
        Icon(
            if (isPlayingAll) Icons.Filled.Stop else Icons.Filled.PlayArrow,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(6.dp))
        Text(
            if (isPlayingAll) "Stop" else "Play All",
            color = contentColor,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

private fun copyAyah(ctx: Context, ayah: Ayah) {
    val cm = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val text = buildString {
        append(ayah.arabic)
        if (ayah.translation.isNotBlank()) { append("\n\n"); append(ayah.translation) }
        append("\n\n— Quran ${ayah.surahNumber}:${ayah.number}")
    }
    cm.setPrimaryClip(ClipData.newPlainText("Ayah ${ayah.surahNumber}:${ayah.number}", text))
}

private fun shareAyah(ctx: Context, ayah: Ayah) {
    val text = buildString {
        append(ayah.arabic)
        if (ayah.translation.isNotBlank()) { append("\n\n"); append(ayah.translation) }
        append("\n\n— Quran ${ayah.surahNumber}:${ayah.number}\nvia Noor-e-Islam")
    }
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    ctx.startActivity(Intent.createChooser(send, "Share ayah").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
}

