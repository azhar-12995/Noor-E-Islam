package com.azhar.noor_e_islam.core.util

/**
 * Wrapper around any data fetched from a remote/network source. Used by repositories.
 */
sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : Resource<Nothing>()
    data object Loading : Resource<Nothing>()
}

inline fun <T, R> Resource<T>.map(transform: (T) -> R): Resource<R> = when (this) {
    is Resource.Success -> Resource.Success(transform(data))
    is Resource.Error   -> this
    Resource.Loading    -> Resource.Loading
}

