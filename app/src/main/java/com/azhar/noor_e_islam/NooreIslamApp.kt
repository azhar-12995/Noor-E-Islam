package com.azhar.noor_e_islam

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Root [Application] for Noor-e-Islam.
 *
 * Initializes:
 *  - Hilt DI graph (via [HiltAndroidApp])
 *  - Firebase services
 *  - Timber for logging in debug
 *  - Default notification channel for FCM / reminders
 */
@HiltAndroidApp
class NooreIslamApp : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // Firebase
        FirebaseApp.initializeApp(this)

        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                getString(R.string.default_notification_channel_id),
                getString(R.string.default_notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily hadith, prayer reminders, habit reminders & Islamic events."
            }
            manager.createNotificationChannel(channel)
        }
    }
}

