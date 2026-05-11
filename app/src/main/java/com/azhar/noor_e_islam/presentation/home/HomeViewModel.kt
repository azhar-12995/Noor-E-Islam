package com.azhar.noor_e_islam.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azhar.noor_e_islam.core.datastore.UserPrefs
import com.azhar.noor_e_islam.core.datastore.UserPreferences
import com.azhar.noor_e_islam.core.util.DateUtils
import com.azhar.noor_e_islam.core.util.Resource
import com.azhar.noor_e_islam.data.remote.firebase.AnnouncementsFirestoreService
import com.azhar.noor_e_islam.domain.model.User
import com.azhar.noor_e_islam.domain.repository.AuthRepository
import com.azhar.noor_e_islam.domain.repository.UserPrefsRepository
import com.azhar.noor_e_islam.domain.usecase.GetSurahsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val userName: String = "Friend",
    val gregorian: String = "",
    val hijri: String = "",
    val lastReadSurah: Int = 1,
    val lastReadAyah: Int = 1,
    /** True while the Quran corpus is being downloaded / synced from REST → Firestore → Room. */
    val isQuranSyncing: Boolean = false,
    val quranSyncError: String? = null,
    val totalSurahsLoaded: Int = 0,
    /** Latest admin announcement to surface as a banner on Home. */
    val latestAnnouncement: AnnouncementsFirestoreService.Announcement? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    auth: AuthRepository,
    prefsRepo: UserPrefsRepository,
    getSurahs: GetSurahsUseCase,
    private val announcements: AnnouncementsFirestoreService,
    private val userPrefs: UserPreferences,
) : ViewModel() {
    private val _state = MutableStateFlow(
        HomeState(gregorian = DateUtils.gregorianToday(), hijri = DateUtils.hijriToday())
    )
    val state: StateFlow<HomeState> = _state

    init {
        // User + last-read prefs
        viewModelScope.launch {
            combine(auth.currentUser, prefsRepo.prefs) { u: User?, p: UserPrefs -> u to p }
                .collect { (user, prefs) ->
                    _state.update {
                        it.copy(
                            userName = user?.name ?: if (user?.isAnonymous == true) "Guest" else "Friend",
                            lastReadSurah = prefs.lastReadSurah,
                            lastReadAyah = prefs.lastReadAyah,
                        )
                    }
                }
        }
        // Trigger Quran sync (ensurePopulated) and observe its progress.
        // The repository emits Loading → Success | Error and is offline-first,
        // so this is a no-op once Room is hydrated.
        viewModelScope.launch {
            getSurahs().collect { res ->
                _state.update {
                    when (res) {
                        is Resource.Loading -> it.copy(isQuranSyncing = true, quranSyncError = null)
                        is Resource.Success -> it.copy(
                            isQuranSyncing = false,
                            quranSyncError = null,
                            totalSurahsLoaded = res.data.size,
                        )
                        is Resource.Error -> it.copy(isQuranSyncing = false, quranSyncError = res.message)
                    }
                }
            }
        }
        // Latest admin announcement — only emit if the user hasn't dismissed
        // this exact announcement yet. After dismissal it lives on in the
        // notifications list, but never re-pops as a dialog on Home.
        viewModelScope.launch {
            val latest = runCatching { announcements.latest(1) }.getOrDefault(emptyList()).firstOrNull()
            val seenId = runCatching { userPrefs.lastSeenAnnouncementId.first() }
                .getOrDefault("")
            val toShow = latest?.takeIf { it.id != seenId }
            _state.update { it.copy(latestAnnouncement = toShow) }
        }
    }

    /** Mark the on-Home announcement dialog as seen so it never re-appears. */
    fun dismissAnnouncement(id: String) {
        viewModelScope.launch {
            runCatching { userPrefs.markAnnouncementSeen(id) }
            _state.update { it.copy(latestAnnouncement = null) }
        }
    }
}
