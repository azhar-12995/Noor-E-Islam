package com.azhar.noor_e_islam.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azhar.noor_e_islam.data.remote.firebase.UserProfileFirestoreService
import com.azhar.noor_e_islam.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditProfileUi(
    val name: String = "",
    val email: String = "",
    val photoBase64: String = "",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val auth: AuthRepository,
    private val profileService: UserProfileFirestoreService,
) : ViewModel() {

    private val _ui = MutableStateFlow(EditProfileUi())
    val ui: StateFlow<EditProfileUi> = _ui.asStateFlow()

    init { load() }

    private fun load() {
        viewModelScope.launch {
            try {
                val user = runCatching { auth.currentUser.first() }.getOrNull()
                val remote = profileService.load()
                _ui.update {
                    it.copy(
                        isLoading = false,
                        name = remote?.name?.ifBlank { user?.name.orEmpty() } ?: user?.name.orEmpty(),
                        email = remote?.email?.ifBlank { user?.email.orEmpty() } ?: user?.email.orEmpty(),
                        photoBase64 = remote?.photoBase64.orEmpty(),
                    )
                }
            } catch (t: Throwable) {
                _ui.update { it.copy(isLoading = false, error = t.message) }
            }
        }
    }

    fun setName(v: String)       = _ui.update { it.copy(name = v, saved = false) }
    fun setPhoto(base64: String) = _ui.update { it.copy(photoBase64 = base64, saved = false) }

    fun save() {
        val s = _ui.value
        if (s.name.isBlank()) {
            _ui.update { it.copy(error = "Name cannot be empty") }
            return
        }
        viewModelScope.launch {
            _ui.update { it.copy(isSaving = true, error = null) }
            val ok = profileService.save(
                UserProfileFirestoreService.ProfileData(
                    name = s.name.trim(),
                    email = s.email,
                    photoBase64 = s.photoBase64,
                )
            )
            _ui.update {
                it.copy(
                    isSaving = false,
                    saved = ok,
                    error = if (ok) null else "Failed to save profile",
                )
            }
        }
    }
}

