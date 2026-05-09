package com.azhar.noor_e_islam.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azhar.noor_e_islam.domain.repository.UserPrefsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val prefsRepo: UserPrefsRepository,
) : ViewModel() {
    fun complete() = viewModelScope.launch { prefsRepo.setOnboardingDone(true) }
}

