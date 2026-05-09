package com.azhar.noor_e_islam.core.util

/** One-shot UI events emitted by ViewModels. */
sealed interface UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent
    data class Navigate(val route: String) : UiEvent
    data object NavigateUp : UiEvent
}

