package com.azhar.noor_e_islam.data.remote.firebase

import com.azhar.noor_e_islam.domain.model.EventCategory
import com.azhar.noor_e_islam.domain.model.IslamicEvent
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firestore layout for Islamic events:
 *
 *  islamic_events/{eventId}                    — single event document
 *      ├─ id, title, hijriDate, gregorianDateIso
 *      ├─ year, month, day, weekday
 *      ├─ category, description
 *      └─ savedAt : Long
 *
 *  events_meta/state                           — sync state
 *      ├─ seededVersion : Int    (bump when seed list changes)
 *      └─ updatedAt     : Long
 *
 *  Using the deterministic id ([IslamicEvent.makeId]) as the Firestore doc id
 *  means [seedIfMissing] can be called any number of times without duplicating
 *  any event — `set(id, data)` simply overwrites the same doc.
 */
@Singleton
class EventsFirestoreService @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    private val events get() = firestore.collection(COL_EVENTS)
    private val meta   get() = firestore.collection(COL_META).document(DOC_STATE)

    suspend fun fetchAll(): List<IslamicEvent> = runCatching {
        val snap = events.get().await()
        snap.documents.mapNotNull { doc ->
            val data = doc.data ?: return@mapNotNull null
            data.toIslamicEvent()
        }
    }.getOrDefault(emptyList())

    suspend fun existingIds(): Set<String> = runCatching {
        events.get().await().documents.map { it.id }.toSet()
    }.getOrDefault(emptySet())

    /** Inserts only the events whose ids are not already present. */
    suspend fun upsertMissing(items: List<IslamicEvent>): Int {
        if (items.isEmpty()) return 0
        val existing = existingIds()
        val toWrite = items.filter { it.id !in existing }
        if (toWrite.isEmpty()) return 0

        toWrite.chunked(450).forEach { chunk ->
            val batch = firestore.batch()
            chunk.forEach { ev -> batch.set(events.document(ev.id), ev.toMap()) }
            batch.commit().await()
        }
        return toWrite.size
    }

    suspend fun loadSeededVersion(): Int = runCatching {
        (meta.get().await().getLong("seededVersion") ?: 0L).toInt()
    }.getOrDefault(0)

    suspend fun saveSeededVersion(version: Int) {
        meta.set(
            mapOf(
                "seededVersion" to version,
                "updatedAt" to System.currentTimeMillis(),
            )
        ).await()
    }

    private companion object {
        const val COL_EVENTS = "islamic_events"
        const val COL_META   = "events_meta"
        const val DOC_STATE  = "state"
    }
}

/* ----------------------- Mappers ----------------------- */

private fun IslamicEvent.toMap(): Map<String, Any?> = mapOf(
    "id" to id,
    "title" to title,
    "hijriDate" to hijriDate,
    "gregorianDateIso" to gregorianDateIso,
    "year" to year,
    "month" to month,
    "day" to day,
    "weekday" to weekday,
    "category" to category.name,
    "description" to description,
    "savedAt" to System.currentTimeMillis(),
)

private fun Map<String, Any?>.toIslamicEvent(): IslamicEvent? {
    val id = this["id"] as? String ?: return null
    val title = this["title"] as? String ?: return null
    val gregorian = this["gregorianDateIso"] as? String ?: return null
    return IslamicEvent(
        id = id,
        title = title,
        hijriDate = this["hijriDate"] as? String ?: "",
        gregorianDateIso = gregorian,
        year = (this["year"] as? Long)?.toInt() ?: 0,
        month = (this["month"] as? Long)?.toInt() ?: 0,
        day = (this["day"] as? Long)?.toInt() ?: 0,
        weekday = this["weekday"] as? String ?: "",
        category = runCatching {
            EventCategory.valueOf(this["category"] as? String ?: "GENERAL")
        }.getOrDefault(EventCategory.GENERAL),
        description = this["description"] as? String ?: "",
    )
}

