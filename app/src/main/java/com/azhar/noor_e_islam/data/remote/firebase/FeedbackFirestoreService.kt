package com.azhar.noor_e_islam.data.remote.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firestore layout for user feedback:
 *
 *  feedback/{feedbackId}
 *    ├─ uid          : String
 *    ├─ userName     : String
 *    ├─ text         : String
 *    ├─ images       : List<String>   (base64 JPEGs)
 *    ├─ status       : String   (OPEN | RESPONDED)
 *    ├─ adminResponse: String?
 *    ├─ respondedAt  : Long?
 *    └─ createdAt    : Long
 *
 * Security rules (define in Firestore console):
 *   match /feedback/{id} {
 *     allow create: if request.auth != null
 *                && request.resource.data.uid == request.auth.uid;
 *     allow read:   if request.auth != null
 *                && (resource.data.uid == request.auth.uid
 *                    || request.auth.token.admin == true);
 *     allow update: if request.auth != null
 *                && request.auth.token.admin == true;
 *   }
 *
 * → Only the author OR a user with the `admin` custom claim can read.
 * → Only admins can write [adminResponse] / change [status].
 */
@Singleton
class FeedbackFirestoreService @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
) {
    private val col get() = firestore.collection(COL)

    /** Submits new feedback; returns the new doc id or null on failure. */
    suspend fun submit(text: String, images: List<String>, userName: String): String? {
        val uid = auth.currentUser?.uid ?: return null
        val data = mapOf(
            "uid" to uid,
            "userName" to userName,
            "text" to text,
            "images" to images,
            "status" to STATUS_OPEN,
            "adminResponse" to null,
            "respondedAt" to null,
            "createdAt" to System.currentTimeMillis(),
        )
        val doc = col.document()
        return runCatching {
            doc.set(data).await()
            doc.id
        }.getOrNull()
    }

    /** Loads the current user's feedback history, newest first.
     *  We deliberately skip server-side orderBy to avoid requiring a composite
     *  Firestore index (uid + createdAt) — sort client-side instead. */
    suspend fun myFeedback(): List<FeedbackEntry> {
        val uid = auth.currentUser?.uid ?: return emptyList()
        return runCatching {
            col.whereEqualTo("uid", uid)
                .get().await().documents
                .mapNotNull { it.toEntry() }
                .sortedByDescending { it.createdAt }
        }.getOrDefault(emptyList())
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toEntry(): FeedbackEntry? {
        val text = getString("text") ?: return null
        @Suppress("UNCHECKED_CAST")
        val images = (get("images") as? List<String>).orEmpty()
        return FeedbackEntry(
            id = id,
            uid = getString("uid").orEmpty(),
            userName = getString("userName").orEmpty(),
            text = text,
            images = images,
            status = getString("status") ?: STATUS_OPEN,
            adminResponse = getString("adminResponse"),
            respondedAt = getLong("respondedAt"),
            createdAt = getLong("createdAt") ?: 0L,
        )
    }

    /** Admin-only: every feedback document in the system, newest first. */
    suspend fun allFeedback(): List<FeedbackEntry> = runCatching {
        col.get().await().documents
            .mapNotNull { it.toEntry() }
            .sortedByDescending { it.createdAt }
    }.getOrDefault(emptyList())

    /** Admin-only: write a reply on a feedback document. */
    suspend fun respond(feedbackId: String, response: String): Boolean {
        if (response.isBlank()) return false
        val patch = mapOf(
            "status" to STATUS_RESPONDED,
            "adminResponse" to response.trim(),
            "respondedAt" to System.currentTimeMillis(),
        )
        return runCatching {
            col.document(feedbackId).update(patch).await()
        }.isSuccess
    }

    data class FeedbackEntry(
        val id: String,
        val uid: String = "",
        val userName: String = "",
        val text: String,
        val images: List<String>,
        val status: String,
        val adminResponse: String?,
        val respondedAt: Long?,
        val createdAt: Long,
    ) {
        val isResponded get() = status == STATUS_RESPONDED || !adminResponse.isNullOrBlank()
    }

    private companion object {
        const val COL = "feedback"
        const val STATUS_OPEN = "OPEN"
        const val STATUS_RESPONDED = "RESPONDED"
    }
}

