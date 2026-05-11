package com.azhar.noor_e_islam.data.remote.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Reads admin-published announcements from Firestore:
 *
 *   announcements/{id}
 *     ├─ title     : String
 *     ├─ message   : String
 *     ├─ createdAt : Long
 *     └─ category  : String  (Announcement | Event | …)
 */
@Singleton
class AnnouncementsFirestoreService @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    suspend fun latest(limit: Long = 20): List<Announcement> = runCatching {
        firestore.collection(COL)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limit)
            .get().await()
            .documents
            .mapNotNull { d ->
                val title = d.getString("title") ?: return@mapNotNull null
                Announcement(
                    id = d.id,
                    title = title,
                    message = d.getString("message").orEmpty(),
                    category = d.getString("category") ?: "Announcement",
                    createdAt = d.getLong("createdAt") ?: 0L,
                )
            }
    }.getOrDefault(emptyList())

    /** Admin-only: publish a new announcement that lands in every user's
     *  home banner + notifications. Firestore rules must restrict writes
     *  to the admin custom-claim / email. */
    suspend fun publish(title: String, message: String, category: String = "Announcement"): String? {
        if (title.isBlank()) return null
        val doc = firestore.collection(COL).document()
        val data = mapOf(
            "title" to title.trim(),
            "message" to message.trim(),
            "category" to category,
            "createdAt" to System.currentTimeMillis(),
        )
        return runCatching {
            doc.set(data).await()
            doc.id
        }.getOrNull()
    }

    suspend fun delete(id: String): Boolean = runCatching {
        firestore.collection(COL).document(id).delete().await()
    }.isSuccess

    data class Announcement(
        val id: String,
        val title: String,
        val message: String,
        val category: String,
        val createdAt: Long,
    )

    private companion object { const val COL = "announcements" }
}

