package com.azhar.noor_e_islam.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azhar.noor_e_islam.core.util.Resource
import com.azhar.noor_e_islam.domain.usecase.LoginUseCase
import com.azhar.noor_e_islam.domain.usecase.SignInAnonymouslyUseCase
import com.azhar.noor_e_islam.presentation.auth.AuthFormState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val login: LoginUseCase,
    private val anon: SignInAnonymouslyUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(AuthFormState())
    val state: StateFlow<AuthFormState> = _state

    fun onEmail(v: String)    = _state.update { it.copy(email = v, errorMessage = null) }
    fun onPassword(v: String) = _state.update { it.copy(password = v, errorMessage = null) }

    fun submit() {
        val s = _state.value
        if (s.email.isBlank() || s.password.isBlank()) {
            _state.update { it.copy(errorMessage = "Please enter email and password") }; return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            when (val r = login(s.email.trim(), s.password)) {
                is Resource.Success -> _state.update { it.copy(isLoading = false, success = true) }
                is Resource.Error   -> _state.update { it.copy(isLoading = false, errorMessage = r.message) }
                else -> Unit
            }
        }
    }

    fun guest() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val r = anon()) {
                is Resource.Success -> _state.update { it.copy(isLoading = false, success = true) }
                is Resource.Error   -> _state.update { it.copy(isLoading = false, errorMessage = r.message) }
                else -> Unit
            }
        }
    }
}

