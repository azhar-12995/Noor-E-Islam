package com.azhar.noor_e_islam.presentation.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azhar.noor_e_islam.core.util.Resource
import com.azhar.noor_e_islam.domain.usecase.RegisterUseCase
import com.azhar.noor_e_islam.presentation.auth.AuthFormState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val register: RegisterUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(AuthFormState())
    val state: StateFlow<AuthFormState> = _state

    fun onName(v: String)    = _state.update { it.copy(name = v, errorMessage = null) }
    fun onEmail(v: String)   = _state.update { it.copy(email = v, errorMessage = null) }
    fun onPassword(v: String)= _state.update { it.copy(password = v, errorMessage = null) }
    fun onConfirm(v: String) = _state.update { it.copy(confirmPassword = v, errorMessage = null) }

    fun submit() {
        val s = _state.value
        if (s.name.isBlank() || s.email.isBlank() || s.password.length < 6) {
            _state.update { it.copy(errorMessage = "Please fill all fields. Password ≥ 6 chars.") }; return
        }
        if (s.password != s.confirmPassword) {
            _state.update { it.copy(errorMessage = "Passwords do not match") }; return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val r = register(s.name.trim(), s.email.trim(), s.password)) {
                is Resource.Success -> _state.update { it.copy(isLoading = false, success = true) }
                is Resource.Error   -> _state.update { it.copy(isLoading = false, errorMessage = r.message) }
                else -> Unit
            }
        }
    }
}

