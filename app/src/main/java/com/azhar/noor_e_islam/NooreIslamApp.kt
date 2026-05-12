package com.azhar.noor_e_islam

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.azhar.noor_e_islam.core.notifications.HadithDailyWorker
import com.azhar.noor_e_islam.core.notifications.NotificationChannels
import com.azhar.noor_e_islam.core.notifications.PrayerAlarmScheduler
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

/**
 * Root [Application] for Noor-e-Islam.
 *
 * Initializes:
 *  - Hilt DI graph (via [HiltAndroidApp])
 *  - Hilt's [HiltWorkerFactory] so `@HiltWorker` classes (e.g. [HadithDailyWorker])
 *    can be DI-instantiated by WorkManager. Without implementing
 *    [Configuration.Provider] here, WorkManager falls back to its default
 *    factory and our injected workers silently fail to start.
 *  - Firebase services
 *  - Timber for logging in debug
 *  - All notification channels (default / prayer / hadith)
 *  - Daily-hadith [HadithDailyWorker] periodic schedule
 *  - Prayer-time [AlarmManager] schedule for the next 24h
 */
@HiltAndroidApp
class NooreIslamApp : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(if (BuildConfig.DEBUG) Log.DEBUG else Log.INFO)
            .build()

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // Firebase
        FirebaseApp.initializeApp(this)

        // All channels (default / prayer / hadith) must exist BEFORE we post
        // any notification, otherwise nothing shows on API 26+.
        NotificationChannels.ensureAll(this)

        // Schedule daily hadith at 10:00 AM (idempotent via uniqueWork). The
        // worker fires ONLY at 10 AM each day — never on app launch — so the
        // user isn't spammed with a notification every time they open the app.
        HadithDailyWorker.ensureScheduled(this)

        // Re-arm prayer alarms for the next 24h (no-op if no cached location yet).
        PrayerAlarmScheduler.scheduleNextDay(this)
    }
}


