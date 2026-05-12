package com.azhar.noor_e_islam.core.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.azhar.noor_e_islam.core.datastore.UserPreferences
import com.azhar.noor_e_islam.presentation.prayertimes.PrayerTimesCalculator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/**
 * Schedules a wave of [AlarmManager] alarms — one per prayer — for the next
 * 24h. Pulls the user's cached (lat,lng) from [UserPreferences] so it works
 * even after the app process is killed.
 *
 * Re-armed by:
 *  - Application startup (NooreIslamApp.onCreate)
 *  - [BootReceiver] after device reboot
 *  - [PrayerAlarmReceiver] after the last (Isha) alarm of the day fires
 */
object PrayerAlarmScheduler {

    private const val REQ_BASE = 33_000      // per-prayer requestCode offset

    /** Convenience used from receivers (no Hilt available there). */
    fun scheduleNextDay(context: Context) {
        // Build a one-shot CoroutineScope on Default — receiver context is fine.
        CoroutineScope(Dispatchers.Default).launch {
            schedule(context)
        }
    }

    /** Schedules all upcoming prayers for [from] + 24h. Cancels any stale ones first. */
    suspend fun schedule(context: Context, from: Long = System.currentTimeMillis()) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val prefs = UserPreferences(context)
        val loc = runCatching { prefs.cachedLocation.first() }.getOrNull() ?: return
        val (lat, lng) = loc

        val tz = TimeZone.getDefault()
        val now = Calendar.getInstance().apply { timeInMillis = from }
        val timeFmt = SimpleDateFormat("hh:mm a", Locale.getDefault())

        // Cancel any previously-scheduled prayer alarms so we start clean.
        cancelAll(context)

        val targets = mutableListOf<Triple<PrayerTimesCalculator.Prayer, Long, String>>()
        // Compute for today and tomorrow so we have at least 24h of coverage.
        for (dayOffset in 0..1) {
            val day = (now.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, dayOffset) }
            val times = PrayerTimesCalculator.compute(
                date = day.time,
                latitude = lat,
                longitude = lng,
                timeZone = tz,
            )
            PrayerTimesCalculator.Prayer.entries.forEach { p ->
                if (p == PrayerTimesCalculator.Prayer.SUNRISE) return@forEach
                val h = times[p]
                val hh = h.toInt()
                val mm = ((h - hh) * 60).toInt()
                val t = (day.clone() as Calendar).apply {
                    set(Calendar.HOUR_OF_DAY, hh)
                    set(Calendar.MINUTE, mm)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                if (t.timeInMillis > from && t.timeInMillis < from + 24L * 3600 * 1000) {
                    targets += Triple(p, t.timeInMillis, timeFmt.format(t.time))
                }
            }
        }

        targets.sortBy { it.second }
        val lastIndex = targets.lastIndex

        targets.forEachIndexed { index, (prayer, atMillis, label) ->
            val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                action = PrayerAlarmReceiver.ACTION_PRAYER_ALARM
                putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_NAME, prayer.displayName)
                putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_TIME, label)
                putExtra(PrayerAlarmReceiver.EXTRA_NOTIF_ID, REQ_BASE + prayer.ordinal)
                putExtra(PrayerAlarmReceiver.EXTRA_LAST_OF_DAY, index == lastIndex)
            }
            val pi = PendingIntent.getBroadcast(
                context,
                REQ_BASE + prayer.ordinal + (atMillis / 86_400_000).toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

            // Exact + while-idle for forward compatibility with Doze on API 23+.
            // USE_EXACT_ALARM is declared in the manifest so canScheduleExactAlarms
            // is true on Android 12+ for prayer-reminder use case.
            runCatching {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) {
                    am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, atMillis, pi)
                } else {
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, atMillis, pi)
                }
            }
        }
    }

    private fun cancelAll(context: Context) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        PrayerTimesCalculator.Prayer.entries.forEach { p ->
            // Cancel for both "today" and "tomorrow" request-code variants.
            val nowDay = (System.currentTimeMillis() / 86_400_000).toInt()
            listOf(0, 1).forEach { offset ->
                val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                    action = PrayerAlarmReceiver.ACTION_PRAYER_ALARM
                }
                val pi = PendingIntent.getBroadcast(
                    context,
                    REQ_BASE + p.ordinal + nowDay + offset,
                    intent,
                    PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
                )
                if (pi != null) am.cancel(pi)
            }
        }
    }
}

