package com.azhar.noor_e_islam.core.notifications

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.azhar.noor_e_islam.MainActivity
import com.azhar.noor_e_islam.R
import com.azhar.noor_e_islam.core.datastore.UserPreferences
import com.azhar.noor_e_islam.data.repository.HadithRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Posts a "Hadith of the day" notification each morning, even when the app
 * has been killed. Backed by WorkManager (survives reboot via JobScheduler).
 */
@HiltWorker
class HadithDailyWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val hadithRepo: HadithRepository,
    private val prefs: UserPreferences,
) : CoroutineWorker(appContext, params) {

    @SuppressLint("MissingPermission")
    override suspend fun doWork(): Result {
        android.util.Log.i(TAG, "doWork() started")
        val ctx = applicationContext

        // Same-day guard: at most ONE hadith notification per calendar day,
        // regardless of how many times the worker is enqueued (e.g. after a
        // device reboot mid-afternoon).
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val lastDate = runCatching { prefs.hadithNotifDate.first() }.getOrDefault("")
        if (lastDate == today) {
            android.util.Log.i(TAG, "already notified today ($today) — skipping")
            return Result.success()
        }

        NotificationChannels.ensureAll(ctx)

        // Try to load today's hadith. If it fails (no network, repo error, etc.)
        // we STILL post a notification so the wiring can be verified — and we
        // return success so WorkManager doesn't loop on retries.
        val fetched = runCatching { hadithRepo.todayHadith() }
        val hadith = fetched.getOrNull()?.getOrNull()
        if (hadith == null) {
            android.util.Log.w(
                TAG,
                "todayHadith() failed (outer=${fetched.exceptionOrNull()?.message})",
            )
        } else {
            android.util.Log.i(TAG, "todayHadith() ok id=${hadith.id}")
        }

        val tap = Intent(ctx, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            data = android.net.Uri.parse("noor://hadith")
            putExtra(EXTRA_OPEN_ROUTE, ROUTE_HADITH)
        }
        val pi = PendingIntent.getActivity(
            ctx, NOTIF_ID, tap,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val body = hadith?.let {
            it.english.ifBlank { it.urdu }.ifBlank { it.arabic }
        } ?: "Tap to read today's hadith."

        val notification = NotificationCompat.Builder(ctx, NotificationChannels.HADITH_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Hadith of the Day")
            .setContentText(body.take(80))
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pi)
            .build()

        runCatching {
            NotificationManagerCompat.from(ctx).notify(NOTIF_ID, notification)
            android.util.Log.i(TAG, "notification posted id=$NOTIF_ID")
        }.onFailure {
            android.util.Log.e(TAG, "notify() threw", it)
        }
        // Persist today's date so we don't notify twice on the same day.
        runCatching { prefs.setHadithNotifDate(today) }
        return Result.success()
    }

    companion object {
        private const val TAG = "HadithDailyWorker"
        const val WORK_NAME = "noor_daily_hadith"
        const val EXTRA_OPEN_ROUTE = "noor.extra.open_route"
        const val ROUTE_HADITH = "hadith"
        private const val NOTIF_ID = 4101

        /** Schedule (idempotent) a daily 10:00 AM fetch+notify cycle. */
        fun ensureScheduled(context: Context) {
            val request = PeriodicWorkRequestBuilder<HadithDailyWorker>(1, TimeUnit.DAYS)
                .setInitialDelay(initialDelayMillis(targetHour = 10), TimeUnit.MILLISECONDS)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                // UPDATE so the new 10:00 AM time replaces any previously-scheduled
                // (e.g. 08:00) periodic work that used the older constant.
                ExistingPeriodicWorkPolicy.UPDATE,
                request,
            )
        }

        /**
         * Fires the worker once, immediately. Used on app startup so a user can
         * verify the wiring (Hilt factory + channel + notification) without
         * waiting until 10 AM. Uses REPLACE so every launch re-runs it — that
         * keeps things robust if a previous run failed (e.g. transient network
         * error during the first install).
         */
        fun runOnceNow(context: Context) {
            android.util.Log.i(TAG, "runOnceNow() enqueueing one-shot")
            val oneShot = OneTimeWorkRequestBuilder<HadithDailyWorker>().build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                "${WORK_NAME}_once",
                ExistingWorkPolicy.REPLACE,
                oneShot,
            )
        }

        private fun initialDelayMillis(targetHour: Int): Long {
            val now = Calendar.getInstance()
            val target = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, targetHour)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (timeInMillis <= now.timeInMillis) add(Calendar.DAY_OF_YEAR, 1)
            }
            return target.timeInMillis - now.timeInMillis
        }
    }
}

