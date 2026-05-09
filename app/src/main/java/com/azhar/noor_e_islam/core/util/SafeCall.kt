package com.azhar.noor_e_islam.core.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

/**
 * Wraps a suspending block in a [Flow] that emits Loading → Success | Error.
 * Used by repository implementations to standardize result streams.
 */
inline fun <T> safeFlow(crossinline block: suspend () -> T): Flow<Resource<T>> = flow {
    emit(Resource.Loading)
    try {
        emit(Resource.Success(block()))
    } catch (t: Throwable) {
        Timber.e(t, "safeFlow failure")
        emit(Resource.Error(t.localizedMessage ?: "Unknown error", t))
    }
}

/** Run a suspending block and capture failures into [Resource]. */
suspend inline fun <T> safeCall(crossinline block: suspend () -> T): Resource<T> = try {
    Resource.Success(block())
} catch (t: Throwable) {
    Timber.e(t, "safeCall failure")
    Resource.Error(t.localizedMessage ?: "Unknown error", t)
}

