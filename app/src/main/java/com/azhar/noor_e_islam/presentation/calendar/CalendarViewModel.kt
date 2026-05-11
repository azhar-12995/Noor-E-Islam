package com.azhar.noor_e_islam.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azhar.noor_e_islam.core.notifications.EventNotificationScheduler
import com.azhar.noor_e_islam.data.repository.IslamicEventsRepository
import com.azhar.noor_e_islam.domain.model.IslamicEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

data class CalendarUiState(
    val isLoading: Boolean = true,
    val visibleYear: Int = 0,
    val visibleMonth: Int = 0,           // 1..12
    val today: Triple<Int, Int, Int> = Triple(0, 0, 0), // year, month, day
    val selected: Triple<Int, Int, Int> = Triple(0, 0, 0),
    val monthEvents: Map<Int, List<IslamicEvent>> = emptyMap(), // day -> events
    val selectedDayEvents: List<IslamicEvent> = emptyList(),
    val upcoming: List<IslamicEvent> = emptyList(),
    val error: String? = null,
)

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repo: IslamicEventsRepository,
    private val scheduler: EventNotificationScheduler,
) : ViewModel() {

    private val _ui = MutableStateFlow(CalendarUiState())
    val ui: StateFlow<CalendarUiState> = _ui.asStateFlow()

    private val isoFmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    init {
        val cal = Calendar.getInstance()
        val y = cal.get(Calendar.YEAR)
        val m = cal.get(Calendar.MONTH) + 1
        val d = cal.get(Calendar.DAY_OF_MONTH)
        _ui.update {
            it.copy(
                visibleYear = y, visibleMonth = m,
                today = Triple(y, m, d),
                selected = Triple(y, m, d),
            )
        }
        load()
    }

    private fun load() {
        viewModelScope.launch {
            try {
                repo.ensureLoaded()
                // Pre-generate events for the current and next two years so the
                // user sees indicators immediately when scrolling forward.
                val y = _ui.value.visibleYear
                val events = repo.ensureYearLoaded(y) +
                             repo.ensureYearLoaded(y + 1) +
                             repo.ensureYearLoaded(y + 2)
                scheduler.scheduleAll(events)
                recompute()
            } catch (t: Throwable) {
                _ui.update { it.copy(isLoading = false, error = t.message) }
            }
        }
    }

    fun selectDate(year: Int, month: Int, day: Int) {
        _ui.update { it.copy(selected = Triple(year, month, day)) }
        recompute()
    }

    fun prevMonth() {
        _ui.update {
            val (y, m) = decrementMonth(it.visibleYear, it.visibleMonth)
            it.copy(visibleYear = y, visibleMonth = m)
        }
        ensureVisibleYearLoaded()
    }

    fun nextMonth() {
        _ui.update {
            val (y, m) = incrementMonth(it.visibleYear, it.visibleMonth)
            it.copy(visibleYear = y, visibleMonth = m)
        }
        ensureVisibleYearLoaded()
    }

    /** Lazy-load events for whatever year just became visible, then refresh UI. */
    private fun ensureVisibleYearLoaded() {
        viewModelScope.launch {
            runCatching { repo.ensureYearLoaded(_ui.value.visibleYear) }
            recompute()
        }
    }

    private fun recompute() {
        val s = _ui.value
        val monthEvents = repo.eventsForMonth(s.visibleYear, s.visibleMonth)
            .groupBy { it.day }
        val selectedIso = iso(s.selected.first, s.selected.second, s.selected.third)
        val todayIso = iso(s.today.first, s.today.second, s.today.third)
        _ui.update {
            it.copy(
                isLoading = false,
                monthEvents = monthEvents,
                selectedDayEvents = repo.eventsForDate(selectedIso),
                upcoming = repo.upcoming(todayIso, limit = 4),
                error = null,
            )
        }
    }

    private fun iso(y: Int, m: Int, d: Int): String =
        "%04d-%02d-%02d".format(y, m, d)

    private fun decrementMonth(y: Int, m: Int): Pair<Int, Int> =
        if (m == 1) y - 1 to 12 else y to m - 1

    private fun incrementMonth(y: Int, m: Int): Pair<Int, Int> =
        if (m == 12) y + 1 to 1 else y to m + 1
}
