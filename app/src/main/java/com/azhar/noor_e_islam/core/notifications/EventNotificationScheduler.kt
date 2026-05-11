package com.azhar.noor_e_islam.core.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.azhar.noor_e_islam.domain.model.IslamicEvent
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Schedules one local alarm per upcoming Islamic event using [AlarmManager].
 *
 * Why local alarms (and not FCM)?
 *  - Event dates are known in advance and identical for all users in the same TZ.
 *  - Works fully offline once seeded; no backend job needed.
 *
 * Alarms fire at 08:00 local time on each event's Gregorian date.
 */
@Singleton
class EventNotificationScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val alarmManager: AlarmManager? =
        context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

    fun scheduleAll(events: List<IslamicEvent>) {
        val am = alarmManager ?: return
        val now = System.currentTimeMillis()
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        events.forEach { ev ->
            val date = runCatching { fmt.parse(ev.gregorianDateIso) }.getOrNull() ?: return@forEach
            val cal = Calendar.getInstance().apply {
                time = date
                set(Calendar.HOUR_OF_DAY, 8)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            if (cal.timeInMillis <= now) return@forEach // already past

            val intent = Intent(context, EventAlarmReceiver::class.java).apply {
                action = EventAlarmReceiver.ACTION_EVENT_ALARM
                putExtra(EventAlarmReceiver.EXTRA_EVENT_ID, ev.id)
                putExtra(EventAlarmReceiver.EXTRA_EVENT_TITLE, ev.title)
                putExtra(EventAlarmReceiver.EXTRA_EVENT_HIJRI, ev.hijriDate)
                putExtra(EventAlarmReceiver.EXTRA_EVENT_DESC, ev.description)
            }
            val pi = PendingIntent.getBroadcast(
                context,
                ev.id.hashCode(),
                intent,
                pendingFlags(),
            )

            // Use inexact for forward compatibility (no SCHEDULE_EXACT_ALARM needed).
            am.set(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pi)
        }
    }

    private fun pendingFlags(): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        else
            PendingIntent.FLAG_UPDATE_CURRENT
}

