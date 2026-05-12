package com.azhar.noor_e_islam.core.notifications

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.azhar.noor_e_islam.MainActivity
import com.azhar.noor_e_islam.R

/**
 * Fires when an [android.app.AlarmManager] alarm for a prayer time elapses.
 * Posts a high-priority notification on the dedicated prayer channel.
 *
 * On the final alarm of the day ([EXTRA_LAST_OF_DAY] = true) the receiver
 * re-arms tomorrow's prayer schedule.
 */
class PrayerAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_PRAYER_ALARM) return

        val name = intent.getStringExtra(EXTRA_PRAYER_NAME) ?: "Prayer"
        val timeText = intent.getStringExtra(EXTRA_PRAYER_TIME).orEmpty()
        val notifId = intent.getIntExtra(EXTRA_NOTIF_ID, name.hashCode())
        val isLast = intent.getBooleanExtra(EXTRA_LAST_OF_DAY, false)

        NotificationChannels.ensureAll(context)

        val tap = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            data = android.net.Uri.parse("noor://prayer-times")
        }
        val pi = PendingIntent.getActivity(
            context, notifId, tap,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, NotificationChannels.PRAYER_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("$name time")
            .setContentText(if (timeText.isBlank()) "It's time for $name." else "$name is at $timeText")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pi)
            .build()

        runCatching {
            NotificationManagerCompat.from(context).notify(notifId, notification)
        }

        // After the last prayer of the day fires, queue tomorrow's batch.
        if (isLast) {
            runCatching { PrayerAlarmScheduler.scheduleNextDay(context) }
        }
    }

    companion object {
        const val ACTION_PRAYER_ALARM = "com.azhar.noor_e_islam.PRAYER_ALARM"
        const val EXTRA_PRAYER_NAME = "prayer_name"
        const val EXTRA_PRAYER_TIME = "prayer_time"
        const val EXTRA_NOTIF_ID    = "notif_id"
        const val EXTRA_LAST_OF_DAY = "last_of_day"
    }
}

