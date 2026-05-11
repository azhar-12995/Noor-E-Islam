package com.azhar.noor_e_islam.presentation.quran.share

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azhar.noor_e_islam.core.util.Resource
import com.azhar.noor_e_islam.domain.usecase.GetAyahsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ShareAyahUi(
    val isLoading: Boolean = true,
    val arabic: String = "",
    val translation: String = "",
    val transliteration: String = "",
    val reference: String = "",
)

/**
 * Loads the actual ayah text from the same Quran repository the reader uses,
 * so the share screen always mirrors what the user just tapped on.
 */
@HiltViewModel
class ShareAyahViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val getAyahs: GetAyahsUseCase,
) : ViewModel() {

    private val surahId: Int = handle.get<Int>("surahId") ?: 1
    private val ayahNumber: Int = handle.get<Int>("ayah") ?: 1

    private val _ui = MutableStateFlow(
        ShareAyahUi(reference = "($surahId:$ayahNumber)")
    )
    val ui: StateFlow<ShareAyahUi> = _ui.asStateFlow()

    init { load() }

    private fun load() {
        viewModelScope.launch {
            getAyahs(surahId).collect { res ->
                if (res is Resource.Success) {
                    val a = res.data.firstOrNull { it.number == ayahNumber }
                    if (a != null) {
                        _ui.value = ShareAyahUi(
                            isLoading = false,
                            arabic = a.arabic,
                            translation = a.translation,
                            transliteration = a.transliteration.orEmpty(),
                            reference = "($surahId:$ayahNumber)",
                        )
                    } else {
                        _ui.value = _ui.value.copy(isLoading = false)
                    }
                } else if (res is Resource.Error) {
                    _ui.value = _ui.value.copy(isLoading = false)
                }
            }
        }
    }
}

