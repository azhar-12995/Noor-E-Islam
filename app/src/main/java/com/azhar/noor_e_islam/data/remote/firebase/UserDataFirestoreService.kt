package com.azhar.noor_e_islam.data.remote.firebase

import com.azhar.noor_e_islam.domain.model.Bookmark
import com.azhar.noor_e_islam.domain.model.BookmarkType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firestore mirror of user-specific data.
 * Layout:
 *   /users/{uid}/bookmarks/{id}
 *   /users/{uid}/progress/current
 */
@Singleton
class UserDataFirestoreService @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
) {
    private fun uid(): String? = auth.currentUser?.uid

    suspend fun pushBookmark(b: Bookmark) {
        val u = uid() ?: return
        runCatching {
            firestore.collection("users").document(u)
                .collection("bookmarks").document(b.id)
                .set(mapOf(
                    "id" to b.id,
                    "type" to b.type.name,
                    "refId" to b.refId,
                    "title" to b.title,
                    "subtitle" to b.subtitle,
                    "createdAt" to b.createdAt,
                )).await()
        }.onFailure { Timber.w(it, "pushBookmark failed") }
    }

    suspend fun deleteBookmark(id: String) {
        val u = uid() ?: return
        runCatching {
            firestore.collection("users").document(u)
                .collection("bookmarks").document(id)
                .delete().await()
        }.onFailure { Timber.w(it, "deleteBookmark failed") }
    }

    suspend fun fetchBookmarks(): List<Bookmark> {
        val u = uid() ?: return emptyList()
        return runCatching {
            firestore.collection("users").document(u)
                .collection("bookmarks").get().await().documents.mapNotNull { d ->
                    Bookmark(
                        id = d.getString("id") ?: return@mapNotNull null,
                        type = runCatching { BookmarkType.valueOf(d.getString("type") ?: "AYAH") }.getOrDefault(BookmarkType.AYAH),
                        refId = d.getString("refId") ?: "",
                        title = d.getString("title") ?: "",
                        subtitle = d.getString("subtitle"),
                        createdAt = d.getLong("createdAt") ?: System.currentTimeMillis(),
                    )
                }
        }.getOrDefault(emptyList())
    }

    suspend fun pushProgress(surah: Int, ayah: Int, totalRead: Int, streakDays: Int) {
        val u = uid() ?: return
        runCatching {
            firestore.collection("users").document(u)
                .collection("progress").document("current")
                .set(mapOf(
                    "lastReadSurah" to surah,
                    "lastReadAyah" to ayah,
                    "totalAyahsRead" to totalRead,
                    "streakDays" to streakDays,
                    "updatedAt" to System.currentTimeMillis(),
                )).await()
        }.onFailure { Timber.w(it, "pushProgress failed") }
    }
}

