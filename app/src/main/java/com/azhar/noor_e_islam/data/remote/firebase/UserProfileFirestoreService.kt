package com.azhar.noor_e_islam.data.remote.firebase

import com.azhar.noor_e_islam.core.security.CryptoUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Stores the user's editable profile (name + base64 photo) in Firestore.
 *
 * Sensitive fields ([name], [email]) are AES-GCM encrypted with the
 * device-bound KeyStore key via [CryptoUtil], so even a Firestore admin
 * cannot read them in plaintext.
 *
 * Layout:
 *   users_profiles/{uid}
 *     ├─ nameEnc   : String  (AES-GCM base64)
 *     ├─ emailEnc  : String  (AES-GCM base64)
 *     ├─ photoB64  : String  (JPEG bytes, base64)
 *     └─ updatedAt : Long
 */
@Singleton
class UserProfileFirestoreService @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val crypto: CryptoUtil,
) {
    private fun ref(uid: String) = firestore.collection(COL).document(uid)

    /** Hot, in-memory cache of the latest profile so any screen observing it
     *  (e.g. the Profile header) updates instantly when [save] is called. */
    private val _profile = MutableStateFlow<ProfileData?>(null)
    val profile: StateFlow<ProfileData?> = _profile.asStateFlow()

    suspend fun load(): ProfileData? {
        val uid = auth.currentUser?.uid ?: return null
        val snap = runCatching { ref(uid).get().await() }.getOrNull() ?: return null
        if (!snap.exists()) return null
        val data = ProfileData(
            name = crypto.decrypt(snap.getString("nameEnc")),
            email = crypto.decrypt(snap.getString("emailEnc")),
            photoBase64 = snap.getString("photoB64").orEmpty(),
        )
        _profile.value = data
        return data
    }

    suspend fun save(data: ProfileData): Boolean {
        val uid = auth.currentUser?.uid ?: return false
        val map = mapOf(
            "nameEnc"   to crypto.encrypt(data.name),
            "emailEnc"  to crypto.encrypt(data.email),
            "photoB64"  to data.photoBase64,
            "updatedAt" to System.currentTimeMillis(),
        )
        val ok = runCatching { ref(uid).set(map).await() }.isSuccess
        if (ok) _profile.value = data
        return ok
    }

    /** Admin-only: list every user's profile. Decrypts on read so the admin
     *  sees plaintext name/email in the dashboard. */
    suspend fun listAll(): List<UserSummary> = runCatching {
        firestore.collection(COL).get().await().documents.map { d ->
            UserSummary(
                uid = d.id,
                name = crypto.decrypt(d.getString("nameEnc")),
                email = crypto.decrypt(d.getString("emailEnc")),
                photoBase64 = d.getString("photoB64").orEmpty(),
                updatedAt = d.getLong("updatedAt") ?: 0L,
            )
        }.sortedByDescending { it.updatedAt }
    }.getOrDefault(emptyList())

    data class UserSummary(
        val uid: String,
        val name: String,
        val email: String,
        val photoBase64: String,
        val updatedAt: Long,
    )

    data class ProfileData(
        val name: String,
        val email: String,
        val photoBase64: String,
    )

    private companion object { const val COL = "users_profiles" }
}

