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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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
) : ViewModel() {
    private val _state = MutableStateFlow(QuranReaderState(surahId = handle.get<Int>("surahId") ?: 1))
    val state: StateFlow<QuranReaderState> = _state

    /** Set of bookmarked refIds, exposed as flow so UI can highlight. */
    val bookmarkedRefs: StateFlow<Set<String>> = bookmarkRepo.observeAll()
        .map { list -> list.filter { it.type == BookmarkType.AYAH }.map { it.refId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    val currentAudioUrl get() = audio.currentUrl
    val isAudioPlaying  get() = audio.isPlaying

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

    fun togglePlay(ayah: Ayah) {
        // effectiveAudioUrl falls back to the alquran CDN constructed from globalNumber
        // → audio works for EVERY ayah of EVERY surah, even if the cached field is null.
        val url = ayah.effectiveAudioUrl ?: return
        audio.toggle(url)
    }

    fun toggleBookmark(ayah: Ayah) = viewModelScope.launch {
        val refId = "${ayah.surahNumber}:${ayah.number}"
        if (bookmarkedRefs.value.contains(refId)) {
            // Bookmark id == refId for ayah bookmarks (see add() below).
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
        audio.stop()
        super.onCleared()
    }
}
