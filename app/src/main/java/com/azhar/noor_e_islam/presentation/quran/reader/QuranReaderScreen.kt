package com.azhar.noor_e_islam.presentation.quran.reader

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.azhar.noor_e_islam.core.ui.components.AyahCard
import com.azhar.noor_e_islam.core.ui.components.ErrorState
import com.azhar.noor_e_islam.core.ui.components.LoadingState
import com.azhar.noor_e_islam.domain.model.Ayah

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranReaderScreen(
    surahId: Int,
    onBack: () -> Unit,
    onSettings: () -> Unit,
    onShareAyah: (Int) -> Unit,
    viewModel: QuranReaderViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val bookmarked by viewModel.bookmarkedRefs.collectAsState()
    val currentAudio by viewModel.currentAudioUrl.collectAsState()
    val isPlaying by viewModel.isAudioPlaying.collectAsState()
    val ctx = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Surah ${state.surahId}") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
                actions = { IconButton(onClick = onSettings) { Icon(Icons.Outlined.Settings, null) } }
            )
        }
    ) { padding ->
        when {
            state.isLoading      -> LoadingState(modifier = Modifier.padding(padding))
            state.error != null  -> ErrorState(message = state.error!!, modifier = Modifier.padding(padding))
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(state.ayahs, key = { "${it.surahNumber}-${it.number}" }) { ayah ->
                    val refId = "${ayah.surahNumber}:${ayah.number}"
                    val effectiveAudio = ayah.effectiveAudioUrl
                    val isThisPlaying = isPlaying && currentAudio == effectiveAudio && effectiveAudio != null
                    AyahCard(
                        arabic = ayah.arabic,
                        translation = ayah.translation.ifBlank { "" },
                        reference = "${ayah.surahNumber}:${ayah.number}",
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
