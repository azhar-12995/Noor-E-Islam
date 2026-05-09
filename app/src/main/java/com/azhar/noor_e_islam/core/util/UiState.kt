package com.azhar.noor_e_islam.core.util

/**
 * Generic UI state used by simple list/detail screens.
 * Feature screens may wrap this in their own State data class for richer fields.
 */
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data object Empty   : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

