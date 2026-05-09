package com.azhar.noor_e_islam.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azhar.noor_e_islam.domain.repository.AuthRepository
import com.azhar.noor_e_islam.domain.repository.UserPrefsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SplashDestination {
    data object Onboarding : SplashDestination
    data object Auth       : SplashDestination
    data object Home       : SplashDestination
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepo: AuthRepository,
    private val prefsRepo: UserPrefsRepository,
) : ViewModel() {

    private val _destination = MutableStateFlow<SplashDestination?>(null)
    val destination: StateFlow<SplashDestination?> = _destination

    init {
        viewModelScope.launch {
            val prefs = prefsRepo.prefs.first()
            val user = authRepo.currentUser.first()
            _destination.value = when {
                !prefs.onboardingDone -> SplashDestination.Onboarding
                user == null          -> SplashDestination.Auth
                else                  -> SplashDestination.Home
            }
        }
    }
}

