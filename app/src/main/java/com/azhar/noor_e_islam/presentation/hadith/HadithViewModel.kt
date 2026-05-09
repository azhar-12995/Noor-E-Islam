package com.azhar.noor_e_islam.presentation.hadith

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azhar.noor_e_islam.data.repository.DailyHadith
import com.azhar.noor_e_islam.data.repository.HadithRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HadithUiState(
    val isLoading: Boolean = true,
    val hadith: DailyHadith? = null,
    val error: String? = null,
)

@HiltViewModel
class HadithViewModel @Inject constructor(
    private val repository: HadithRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(HadithUiState())
    val state: StateFlow<HadithUiState> = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            repository.todayHadith()
                .onSuccess { _state.value = HadithUiState(isLoading = false, hadith = it) }
                .onFailure { _state.value = HadithUiState(isLoading = false, error = it.message) }
        }
    }
}

