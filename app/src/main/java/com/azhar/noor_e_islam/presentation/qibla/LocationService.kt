package com.azhar.noor_e_islam.presentation.qibla

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.content.ContextCompat
import com.azhar.noor_e_islam.core.datastore.UserPreferences
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Wraps [com.google.android.gms.location.FusedLocationProviderClient] to provide
 * a one-shot [currentLocation] and a streaming [locationUpdates] Flow.
 *
 * Caller is responsible for runtime permission checks before invocation.
 */
@Singleton
class LocationService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefs: UserPreferences,
) {
    private val client = LocationServices.getFusedLocationProviderClient(context)

    fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    /** Returns last-known location, or actively requests a fresh one if it's null.
     *  Also writes the result to [UserPreferences] so background workers can
     *  reuse it when the app process is dead. */
    @SuppressLint("MissingPermission")
    suspend fun currentLocation(): Location? {
        if (!hasLocationPermission()) return null
        val loc = runCatching {
            client.lastLocation.await()
                ?: client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).await()
        }.getOrNull()
        if (loc != null) {
            runCatching { prefs.saveLocation(loc.latitude, loc.longitude) }
        }
        return loc
    }

    /** Streams location updates roughly every 5s. */
    @SuppressLint("MissingPermission")
    fun locationUpdates(): Flow<Location> = callbackFlow {
        if (!hasLocationPermission()) { close(); return@callbackFlow }

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5_000L)
            .setMinUpdateIntervalMillis(2_000L)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { trySend(it) }
            }
        }

        client.requestLocationUpdates(request, callback, Looper.getMainLooper())
        awaitClose { client.removeLocationUpdates(callback) }
    }
}

