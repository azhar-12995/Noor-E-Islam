package com.azhar.noor_e_islam.data.repository

import com.azhar.noor_e_islam.core.util.IoDispatcher
import com.azhar.noor_e_islam.data.remote.firebase.EventsFirestoreService
import com.azhar.noor_e_islam.domain.model.IslamicEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Islamic events.
 *
 * Strategy:
 *  - Events are NOT hardcoded per year. They are derived dynamically from
 *    [HijriEventCatalog] by converting recurring Hijri dates to Gregorian via
 *    [com.azhar.noor_e_islam.core.util.HijriCalendarUtil] (ICU IslamicCalendar).
 *  - For each Gregorian year the user views, we generate the events once and
 *    cache them in memory + Firestore (`islamic_events/{deterministicId}`).
 *  - Deterministic ids guarantee no duplication across launches/devices.
 */
@Singleton
class IslamicEventsRepository @Inject constructor(
    private val firestoreService: EventsFirestoreService,
    @IoDispatcher private val io: CoroutineDispatcher,
) {
    private val mutex = Mutex()

    private val _events = MutableStateFlow<List<IslamicEvent>>(emptyList())
    val events: StateFlow<List<IslamicEvent>> = _events.asStateFlow()

    private val loadedYears = mutableSetOf<Int>()

    /** First-call: pull anything previously seeded into Firestore. */
    suspend fun ensureLoaded(): List<IslamicEvent> = withContext(io) {
        mutex.withLock {
            if (_events.value.isNotEmpty()) return@withLock _events.value
            val remote = firestoreService.fetchAll()
            if (remote.isNotEmpty()) {
                _events.value = remote.sortedBy { it.gregorianDateIso }
                remote.map { it.year }.forEach(loadedYears::add)
            }
            _events.value
        }
    }

    /**
     * Generate + merge events for [gregorianYear] (any year). Idempotent.
     * Pushes any newly-generated events to Firestore using deterministic ids,
     * so the same year on two devices never produces duplicates.
     */
    suspend fun ensureYearLoaded(gregorianYear: Int): List<IslamicEvent> = withContext(io) {
        mutex.withLock {
            if (gregorianYear in loadedYears) return@withLock _events.value

            val generated = HijriEventCatalog.forGregorianYear(gregorianYear)
            if (generated.isNotEmpty()) {
                runCatching { firestoreService.upsertMissing(generated) }
                val byId = (_events.value + generated).associateBy { it.id }
                _events.value = byId.values.sortedBy { it.gregorianDateIso }
            }
            loadedYears += gregorianYear
            _events.value
        }
    }

    fun eventsForMonth(year: Int, month: Int): List<IslamicEvent> =
        _events.value.filter { it.year == year && it.month == month }

    fun eventsForDate(iso: String): List<IslamicEvent> =
        _events.value.filter { it.gregorianDateIso == iso }

    fun upcoming(todayIso: String, limit: Int = 5): List<IslamicEvent> =
        _events.value.filter { it.gregorianDateIso >= todayIso }.take(limit)
}
