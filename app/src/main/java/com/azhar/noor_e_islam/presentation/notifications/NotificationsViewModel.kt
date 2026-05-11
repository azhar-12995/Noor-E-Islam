package com.azhar.noor_e_islam.presentation.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azhar.noor_e_islam.core.datastore.UserPreferences
import com.azhar.noor_e_islam.data.remote.firebase.AnnouncementsFirestoreService
import com.azhar.noor_e_islam.data.remote.firebase.FeedbackFirestoreService
import com.azhar.noor_e_islam.data.repository.HadithRepository
import com.azhar.noor_e_islam.data.repository.IslamicEventsRepository
import com.azhar.noor_e_islam.presentation.prayertimes.PrayerTimesCalculator
import com.azhar.noor_e_islam.presentation.qibla.LocationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

enum class NotifCategory { PRAYER, HADITH, EVENT, ANNOUNCEMENT, FEEDBACK }

data class NotifItem(
    val id: String,
    val category: NotifCategory,
    val title: String,
    val message: String,
    val timestamp: Long,
    /** Optional payload — interpreted by [NotifTarget] for click navigation. */
    val target: NotifTarget = NotifTarget.None,
)

/** Where a notification card routes the user when tapped. */
sealed interface NotifTarget {
    data object None : NotifTarget
    data object Hadith : NotifTarget
    data object Calendar : NotifTarget
    data object PrayerTimes : NotifTarget
    data class Announcement(val id: String) : NotifTarget
    data class Feedback(val id: String) : NotifTarget
}

data class NotificationsUiState(
    val isLoading: Boolean = true,
    val items: List<NotifItem> = emptyList(),
    val error: String? = null,
)

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val hadithRepo: HadithRepository,
    private val eventsRepo: IslamicEventsRepository,
    private val announcements: AnnouncementsFirestoreService,
    private val feedback: FeedbackFirestoreService,
    private val locationService: LocationService,
    private val prefs: UserPreferences,
) : ViewModel() {

    private val _ui = MutableStateFlow(NotificationsUiState())
    val ui: StateFlow<NotificationsUiState> = _ui.asStateFlow()

    init {
        refresh()
        // Visiting the screen clears the badge.
        viewModelScope.launch { runCatching { prefs.markNotificationsSeen() } }
    }

    fun refresh() {
        viewModelScope.launch {
            try {
                val now = System.currentTimeMillis()
                val out = mutableListOf<NotifItem>()

                /* ---- Prayer reminders: today only, upcoming first then current day ---- */
                val loc = runCatching { locationService.currentLocation() }.getOrNull()
                if (loc != null) {
                    val cal = Calendar.getInstance()
                    val times = PrayerTimesCalculator.compute(
                        date = cal.time,
                        latitude = loc.latitude,
                        longitude = loc.longitude,
                        timeZone = TimeZone.getDefault(),
                    )
                    val ymd = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
                    val fmt = SimpleDateFormat("hh:mm a", Locale.getDefault())
                    PrayerTimesCalculator.Prayer.entries.forEach { p ->
                        val h = times[p]
                        val hh = h.toInt()
                        val mm = ((h - hh) * 60).toInt()
                        val target = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, hh)
                            set(Calendar.MINUTE, mm)
                            set(Calendar.SECOND, 0)
                        }
                        // Only show prayers for the rest of today; skip ones already past.
                        if (target.timeInMillis < now) return@forEach
                        out += NotifItem(
                            id = "prayer-$ymd-${p.name}",
                            category = NotifCategory.PRAYER,
                            title = "${p.displayName} prayer",
                            message = "Today at ${fmt.format(target.time)}",
                            timestamp = target.timeInMillis,
                            target = NotifTarget.PrayerTimes,
                        )
                    }
                }

                /* ---- Hadith of the Day ---- */
                val hadith = runCatching { hadithRepo.todayHadith() }.getOrNull()?.getOrNull()
                if (hadith != null) {
                    out += NotifItem(
                        id = "hadith-${hadith.id}",
                        category = NotifCategory.HADITH,
                        title = "Hadith of the Day",
                        message = hadith.english.take(160).ifBlank { hadith.arabic.take(80) },
                        timestamp = now,
                        target = NotifTarget.Hadith,
                    )
                }

                /* ---- Today's Islamic events ---- */
                runCatching {
                    eventsRepo.ensureLoaded()
                    val cal = Calendar.getInstance()
                    eventsRepo.ensureYearLoaded(cal.get(Calendar.YEAR))
                    val iso = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
                    eventsRepo.eventsForDate(iso)
                }.getOrNull()?.forEach { e ->
                    out += NotifItem(
                        id = "event-${e.id}",
                        category = NotifCategory.EVENT,
                        title = e.title,
                        message = "${e.hijriDate} • ${e.weekday}".trim(),
                        timestamp = now,
                        target = NotifTarget.Calendar,
                    )
                }

                /* ---- Admin announcements ---- */
                runCatching { announcements.latest(20) }.getOrNull()?.forEach { a ->
                    out += NotifItem(
                        id = "ann-${a.id}",
                        category = NotifCategory.ANNOUNCEMENT,
                        title = a.title,
                        message = a.message,
                        timestamp = a.createdAt,
                        target = NotifTarget.Announcement(a.id),
                    )
                }

                /* ---- Admin replies to my feedback ---- */
                runCatching { feedback.myFeedback() }.getOrDefault(emptyList())
                    .filter { !it.adminResponse.isNullOrBlank() }
                    .forEach { f ->
                        out += NotifItem(
                            id = "fb-${f.id}",
                            category = NotifCategory.FEEDBACK,
                            title = "Reply to your feedback",
                            message = f.adminResponse?.take(180).orEmpty(),
                            timestamp = f.respondedAt ?: f.createdAt,
                            target = NotifTarget.Feedback(f.id),
                        )
                    }

                _ui.value = NotificationsUiState(
                    isLoading = false,
                    items = out.sortedByDescending { it.timestamp },
                )
            } catch (t: Throwable) {
                _ui.value = NotificationsUiState(isLoading = false, error = t.message)
            }
        }
    }
}
