package com.azhar.noor_e_islam.presentation.auth

import androidx.compose.runtime.Immutable

@Immutable
data class AuthFormState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val success: Boolean = false,
)

