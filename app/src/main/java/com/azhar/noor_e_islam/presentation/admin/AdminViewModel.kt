package com.azhar.noor_e_islam.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azhar.noor_e_islam.data.remote.firebase.AnnouncementsFirestoreService
import com.azhar.noor_e_islam.data.remote.firebase.FeedbackFirestoreService
import com.azhar.noor_e_islam.data.remote.firebase.UserProfileFirestoreService
import com.azhar.noor_e_islam.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class AdminTab { USERS, FEEDBACK, ANNOUNCE }

data class AdminUiState(
    val tab: AdminTab = AdminTab.USERS,
    val users: List<UserProfileFirestoreService.UserSummary> = emptyList(),
    val feedback: List<FeedbackFirestoreService.FeedbackEntry> = emptyList(),
    val announcements: List<AnnouncementsFirestoreService.Announcement> = emptyList(),
    val isLoading: Boolean = false,
    val message: String? = null,
    val error: String? = null,
    val annTitle: String = "",
    val annMessage: String = "",
    val replyDraftFor: String? = null,
    val replyDraftText: String = "",
)

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val users: UserProfileFirestoreService,
    private val feedback: FeedbackFirestoreService,
    private val announcements: AnnouncementsFirestoreService,
    private val auth: AuthRepository,
) : ViewModel() {

    private val _ui = MutableStateFlow(AdminUiState())
    val ui: StateFlow<AdminUiState> = _ui.asStateFlow()

    init { refresh() }

    fun setTab(t: AdminTab) = _ui.update { it.copy(tab = t, message = null, error = null) }

    fun refresh() {
        viewModelScope.launch {
            _ui.update { it.copy(isLoading = true, error = null) }
            val u = runCatching { users.listAll() }.getOrDefault(emptyList())
            val f = runCatching { feedback.allFeedback() }.getOrDefault(emptyList())
            val a = runCatching { announcements.latest(50) }.getOrDefault(emptyList())
            _ui.update { it.copy(isLoading = false, users = u, feedback = f, announcements = a) }
        }
    }

    fun setAnnTitle(v: String)   = _ui.update { it.copy(annTitle = v, error = null) }
    fun setAnnMessage(v: String) = _ui.update { it.copy(annMessage = v, error = null) }

    fun publishAnnouncement() {
        val s = _ui.value
        if (s.annTitle.isBlank()) {
            _ui.update { it.copy(error = "Title is required.") }; return
        }
        viewModelScope.launch {
            _ui.update { it.copy(isLoading = true, error = null) }
            val id = announcements.publish(s.annTitle, s.annMessage)
            if (id != null) {
                _ui.update { it.copy(isLoading = false, annTitle = "", annMessage = "", message = "Announcement published.") }
                refresh()
            } else {
                _ui.update { it.copy(isLoading = false, error = "Failed to publish.") }
            }
        }
    }

    fun deleteAnnouncement(id: String) {
        viewModelScope.launch { if (announcements.delete(id)) refresh() }
    }

    fun openReply(feedbackId: String) = _ui.update { it.copy(replyDraftFor = feedbackId, replyDraftText = "") }
    fun setReplyText(v: String) = _ui.update { it.copy(replyDraftText = v) }
    fun cancelReply() = _ui.update { it.copy(replyDraftFor = null, replyDraftText = "") }

    fun sendReply() {
        val s = _ui.value
        val id = s.replyDraftFor ?: return
        if (s.replyDraftText.isBlank()) return
        viewModelScope.launch {
            val ok = feedback.respond(id, s.replyDraftText)
            if (ok) {
                _ui.update { it.copy(replyDraftFor = null, replyDraftText = "", message = "Reply sent to user.") }
                refresh()
            } else {
                _ui.update { it.copy(error = "Could not send reply.") }
            }
        }
    }

    fun logout(onDone: () -> Unit) = viewModelScope.launch { auth.logout(); onDone() }
}

