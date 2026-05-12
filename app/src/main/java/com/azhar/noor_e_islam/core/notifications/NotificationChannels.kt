package com.azhar.noor_e_islam.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

/**
 * Central registry for the app's notification channels.
 *
 *  - [DEFAULT_CHANNEL_ID]  → events, hadith, announcements (default importance)
 *  - [PRAYER_CHANNEL_ID]   → time-sensitive prayer reminders (high importance)
 */
object NotificationChannels {
    const val DEFAULT_CHANNEL_ID = "noor_default_channel"
    const val PRAYER_CHANNEL_ID  = "noor_prayer_channel"
    const val HADITH_CHANNEL_ID  = "noor_hadith_channel"

    fun ensureAll(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val nm = context.getSystemService(NotificationManager::class.java) ?: return

        nm.createNotificationChannel(
            NotificationChannel(
                DEFAULT_CHANNEL_ID,
                "General",
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply { description = "Islamic events & announcements." }
        )

        nm.createNotificationChannel(
            NotificationChannel(
                PRAYER_CHANNEL_ID,
                "Prayer Times",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "Reminders when each daily prayer time begins."
                enableVibration(true)
            }
        )

        nm.createNotificationChannel(
            NotificationChannel(
                HADITH_CHANNEL_ID,
                "Daily Hadith",
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply { description = "A new hadith every morning." }
        )
    }
}

