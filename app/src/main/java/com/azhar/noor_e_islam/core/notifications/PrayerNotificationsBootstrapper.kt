package com.azhar.noor_e_islam.core.notifications

import android.content.Context
import com.azhar.noor_e_islam.presentation.qibla.LocationService
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Bridge between Compose-side permission flow and the (non-Hilt) prayer
 * [PrayerAlarmScheduler]. Call after the user has just granted
 * `ACCESS_FINE/COARSE_LOCATION` so we can:
 *
 *   1. Fetch a one-shot fix via [LocationService] (which writes lat/lng to
 *      [com.azhar.noor_e_islam.core.datastore.UserPreferences]).
 *   2. Schedule the next 24h of prayer-time alarms.
 *
 * Without this, prayer alarms wouldn't be armed on a fresh install until the
 * user manually visited the Prayer Times screen.
 */
object PrayerNotificationsBootstrapper {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface Deps {
        fun locationService(): LocationService
    }

    fun kickoff(context: Context) {
        val app = context.applicationContext
        val locationService = EntryPointAccessors
            .fromApplication(app, Deps::class.java)
            .locationService()

        CoroutineScope(Dispatchers.Default).launch {
            // Pull a fresh location (or cached last-known) and persist it to
            // UserPreferences so the scheduler can read it.
            runCatching { locationService.currentLocation() }
            // Now (re-)arm prayer alarms for the next 24h.
            runCatching { PrayerAlarmScheduler.schedule(app) }
        }
    }
}

