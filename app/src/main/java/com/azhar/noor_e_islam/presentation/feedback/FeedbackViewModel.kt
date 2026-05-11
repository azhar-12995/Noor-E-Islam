package com.azhar.noor_e_islam.presentation.feedback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azhar.noor_e_islam.data.remote.firebase.FeedbackFirestoreService
import com.azhar.noor_e_islam.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FeedbackUiState(
    val text: String = "",
    val images: List<String> = emptyList(),    // base64 thumbnails
    val isSubmitting: Boolean = false,
    val history: List<FeedbackFirestoreService.FeedbackEntry> = emptyList(),
    val isHistoryLoading: Boolean = true,
    val message: String? = null,
    val error: String? = null,
)

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val service: FeedbackFirestoreService,
    private val auth: AuthRepository,
) : ViewModel() {

    private val _ui = MutableStateFlow(FeedbackUiState())
    val ui: StateFlow<FeedbackUiState> = _ui.asStateFlow()

    init { loadHistory() }

    fun setText(v: String) = _ui.update { it.copy(text = v, message = null, error = null) }

    fun addImage(base64: String) = _ui.update {
        if (base64.isBlank()) it
        else it.copy(images = it.images + base64, message = null)
    }

    fun removeImage(index: Int) = _ui.update {
        if (index !in it.images.indices) it
        else it.copy(images = it.images.toMutableList().also { l -> l.removeAt(index) })
    }

    fun submit() {
        val s = _ui.value
        if (s.text.isBlank() && s.images.isEmpty()) {
            _ui.update { it.copy(error = "Please write feedback or attach an image.") }
            return
        }
        viewModelScope.launch {
            _ui.update { it.copy(isSubmitting = true, error = null, message = null) }
            val userName = runCatching { auth.currentUser.first()?.name.orEmpty() }.getOrDefault("")
            val id = service.submit(text = s.text.trim(), images = s.images, userName = userName)
            if (id != null) {
                _ui.update {
                    it.copy(
                        isSubmitting = false,
                        text = "",
                        images = emptyList(),
                        message = "Thanks — your feedback has been submitted.",
                    )
                }
                loadHistory()
            } else {
                _ui.update {
                    it.copy(isSubmitting = false, error = "Could not submit feedback. Try again.")
                }
            }
        }
    }

    fun loadHistory() {
        viewModelScope.launch {
            _ui.update { it.copy(isHistoryLoading = true) }
            val list = runCatching { service.myFeedback() }.getOrDefault(emptyList())
            _ui.update { it.copy(history = list, isHistoryLoading = false) }
        }
    }
}

