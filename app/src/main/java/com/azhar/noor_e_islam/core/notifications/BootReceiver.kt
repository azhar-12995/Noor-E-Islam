package com.azhar.noor_e_islam.core.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Re-arms prayer-time [AlarmManager] alarms and ensures the daily Hadith
 * [WorkManager] job is enqueued after the device finishes booting or the
 * app is updated. Without this, alarms scheduled before reboot are lost.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_LOCKED_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED -> {
                // Re-schedule the next 24h of prayer alarms.
                PrayerAlarmScheduler.scheduleNextDay(context.applicationContext)
                // Make sure the daily-hadith periodic worker is enqueued.
                runCatching { HadithDailyWorker.ensureScheduled(context.applicationContext) }
            }
        }
    }
}

