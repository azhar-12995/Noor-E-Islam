package com.azhar.noor_e_islam.presentation.quran.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azhar.noor_e_islam.core.util.Resource
import com.azhar.noor_e_islam.domain.model.Surah
import com.azhar.noor_e_islam.domain.usecase.GetSurahsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuranListState(
    val isLoading: Boolean = true,
    val surahs: List<Surah> = emptyList(),
    val query: String = "",
    val error: String? = null,
)

@HiltViewModel
class QuranListViewModel @Inject constructor(
    getSurahs: GetSurahsUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(QuranListState())
    val state: StateFlow<QuranListState> = _state

    init {
        viewModelScope.launch {
            getSurahs().collect { res ->
                _state.update {
                    when (res) {
                        is Resource.Loading -> it.copy(isLoading = true)
                        is Resource.Success -> it.copy(isLoading = false, surahs = res.data, error = null)
                        is Resource.Error   -> it.copy(isLoading = false, error = res.message)
                    }
                }
            }
        }
    }

    fun onQuery(q: String) = _state.update { it.copy(query = q) }

    val filtered: List<Surah>
        get() {
            val q = state.value.query.trim()
            if (q.isEmpty()) return state.value.surahs
            return state.value.surahs.filter { s ->
                s.englishName.contains(q, true) ||
                        s.name.contains(q) ||
                        s.englishMeaning.contains(q, true) ||
                        s.number.toString() == q
            }
        }
}
