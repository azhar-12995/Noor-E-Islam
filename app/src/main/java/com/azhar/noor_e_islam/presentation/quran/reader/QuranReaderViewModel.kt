package com.azhar.noor_e_islam.presentation.quran.reader

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azhar.noor_e_islam.core.audio.AyahAudioPlayer
import com.azhar.noor_e_islam.core.util.Resource
import com.azhar.noor_e_islam.domain.model.Ayah
import com.azhar.noor_e_islam.domain.model.Bookmark
import com.azhar.noor_e_islam.domain.model.BookmarkType
import com.azhar.noor_e_islam.domain.repository.BookmarkRepository
import com.azhar.noor_e_islam.domain.usecase.GetAyahsUseCase
import com.azhar.noor_e_islam.domain.usecase.UpdateLastReadUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuranReaderState(
    val surahId: Int = 1,
    val isLoading: Boolean = true,
    val ayahs: List<Ayah> = emptyList(),
    val error: String? = null,
)

@HiltViewModel
class QuranReaderViewModel @Inject constructor(
    handle: SavedStateHandle,
    getAyahs: GetAyahsUseCase,
    private val updateLastRead: UpdateLastReadUseCase,
    private val bookmarkRepo: BookmarkRepository,
    val audio: AyahAudioPlayer,
    prefsRepo: com.azhar.noor_e_islam.domain.repository.UserPrefsRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(QuranReaderState(surahId = handle.get<Int>("surahId") ?: 1))
    val state: StateFlow<QuranReaderState> = _state

    /** User-selected Quran font scale (0.8x .. 2.0x). */
    val fontScale: StateFlow<Float> = prefsRepo.prefs
        .map { it.quranFontScale }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 1f)

    /** Set of bookmarked refIds, exposed as flow so UI can highlight. */
    val bookmarkedRefs: StateFlow<Set<String>> = bookmarkRepo.observeAll()
        .map { list -> list.filter { it.type == BookmarkType.AYAH }.map { it.refId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    val currentAudioUrl get() = audio.currentUrl
    val isAudioPlaying  get() = audio.isPlaying

    /** True when "Play All" mode is active (sequential playback). */
    private val _isPlayingAll = MutableStateFlow(false)
    val isPlayingAll: StateFlow<Boolean> = _isPlayingAll.asStateFlow()

    /** Index (0-based) of the ayah currently playing in Play-All mode (or null). */
    private val _currentPlayingIndex = MutableStateFlow<Int?>(null)
    val currentPlayingIndex: StateFlow<Int?> = _currentPlayingIndex.asStateFlow()

    /** Optional ayah to scroll to on first load (e.g. opened from Bookmarks). */
    val initialAyahNumber: Int? = handle.get<Int>("ayah")?.takeIf { it > 0 }

    private var playAllJob: Job? = null

    init {
        val surah = _state.value.surahId
        viewModelScope.launch {
            getAyahs(surah).collect { res ->
                _state.update {
                    when (res) {
                        is Resource.Loading -> it.copy(isLoading = true)
                        is Resource.Success -> it.copy(isLoading = false, ayahs = res.data, error = null)
                        is Resource.Error   -> it.copy(isLoading = false, error = res.message)
                    }
                }
            }
        }
    }

    /** Toggle single-ayah playback (also stops Play-All mode). */
    fun togglePlay(ayah: Ayah) {
        if (_isPlayingAll.value) stopPlayAll()
        val url = ayah.effectiveAudioUrl ?: return
        audio.toggle(url)
    }

    /** Start sequential playback from [startIndex]; auto-advances on each track end. */
    fun playAll(startIndex: Int = 0) {
        val ayahs = _state.value.ayahs
        if (ayahs.isEmpty()) return

        playAllJob?.cancel()
        _isPlayingAll.value = true
        playFromIndex(startIndex.coerceIn(0, ayahs.lastIndex))

        // Auto-advance whenever the current track ends naturally.
        playAllJob = viewModelScope.launch {
            audio.completions.collect {
                if (!_isPlayingAll.value) return@collect
                val next = (_currentPlayingIndex.value ?: -1) + 1
                val list = _state.value.ayahs
                if (next >= list.size) {
                    stopPlayAll()
                } else {
                    playFromIndex(next)
                }
            }
        }
    }

    fun stopPlayAll() {
        _isPlayingAll.value = false
        _currentPlayingIndex.value = null
        playAllJob?.cancel()
        playAllJob = null
        audio.stop()
    }

    /** Pause/resume Play-All without losing the queue. */
    fun togglePlayAll() {
        if (_isPlayingAll.value && audio.isPlaying.value) {
            audio.pause()
        } else if (_isPlayingAll.value) {
            // Resume current track
            _currentPlayingIndex.value?.let { idx ->
                _state.value.ayahs.getOrNull(idx)?.effectiveAudioUrl?.let { audio.play(it) }
            }
        } else {
            playAll(0)
        }
    }

    private fun playFromIndex(index: Int) {
        val ayah = _state.value.ayahs.getOrNull(index) ?: run { stopPlayAll(); return }
        val url = ayah.effectiveAudioUrl ?: run {
            // Skip ayahs without audio
            playFromIndex(index + 1); return
        }
        _currentPlayingIndex.value = index
        audio.play(url)
        markRead(ayah.number)
    }

    fun toggleBookmark(ayah: Ayah) = viewModelScope.launch {
        val refId = "${ayah.surahNumber}:${ayah.number}"
        if (bookmarkedRefs.value.contains(refId)) {
            bookmarkRepo.remove(refId)
        } else {
            bookmarkRepo.add(
                Bookmark(
                    id = refId,
                    type = BookmarkType.AYAH,
                    refId = refId,
                    title = "Surah ${ayah.surahNumber}, Ayah ${ayah.number}",
                    subtitle = ayah.translation.take(80),
                )
            )
        }
    }

    fun markRead(ayah: Int) = viewModelScope.launch {
        updateLastRead(_state.value.surahId, ayah)
    }

    override fun onCleared() {
        stopPlayAll()
        audio.stop()
        super.onCleared()
    }
}
