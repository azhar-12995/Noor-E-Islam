package com.azhar.noor_e_islam.core.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.azhar.noor_e_islam.MainActivity
import com.azhar.noor_e_islam.R

/**
 * Receives the scheduled alarm for an Islamic event and posts a local
 * notification routing the user to the Calendar screen.
 */
class EventAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_EVENT_ALARM) return

        val title = intent.getStringExtra(EXTRA_EVENT_TITLE) ?: "Islamic Event"
        val hijri = intent.getStringExtra(EXTRA_EVENT_HIJRI).orEmpty()
        val desc  = intent.getStringExtra(EXTRA_EVENT_DESC).orEmpty()
        val id    = intent.getStringExtra(EXTRA_EVENT_ID).orEmpty()

        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            data = android.net.Uri.parse("noor://calendar")
        }
        val pending = PendingIntent.getActivity(
            context, id.hashCode(), contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val text = buildString {
            if (hijri.isNotBlank()) append(hijri)
            if (desc.isNotBlank()) {
                if (isNotEmpty()) append("\n")
                append(desc)
            }
        }

        val channelId = context.getString(R.string.default_notification_channel_id)
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(if (text.isBlank()) "Today is $title" else hijri.ifBlank { "Islamic Event" })
            .setStyle(NotificationCompat.BigTextStyle().bigText(if (text.isBlank()) "Today is $title" else text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pending)
            .build()

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(id.hashCode(), notification)
    }

    companion object {
        const val ACTION_EVENT_ALARM = "com.azhar.noor_e_islam.EVENT_ALARM"
        const val EXTRA_EVENT_ID    = "event_id"
        const val EXTRA_EVENT_TITLE = "event_title"
        const val EXTRA_EVENT_HIJRI = "event_hijri"
        const val EXTRA_EVENT_DESC  = "event_desc"
    }
}

