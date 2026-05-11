package com.azhar.noor_e_islam.core.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AES-GCM 256 encryption backed by the Android KeyStore.
 *
 * The secret key never leaves the secure hardware-backed KeyStore. This is
 * used to protect any user-private fields we cache locally (e.g. display name,
 * email) and to encrypt sensitive fields we write to Firestore (so even an
 * admin browsing the DB cannot read them).
 *
 * Output format for [encrypt]:
 *   base64( iv (12B) || ciphertext+tag )
 */
@Singleton
class CryptoUtil @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    /** Encrypts [plaintext]; returns base64 payload (or empty for null/blank input). */
    fun encrypt(plaintext: String?): String {
        if (plaintext.isNullOrBlank()) return ""
        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        }
        val iv = cipher.iv
        val ct = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
        val out = ByteArray(iv.size + ct.size)
        System.arraycopy(iv, 0, out, 0, iv.size)
        System.arraycopy(ct, 0, out, iv.size, ct.size)
        return Base64.encodeToString(out, Base64.NO_WRAP)
    }

    /** Decrypts a payload produced by [encrypt]; returns empty string on failure. */
    fun decrypt(payload: String?): String {
        if (payload.isNullOrBlank()) return ""
        return runCatching {
            val data = Base64.decode(payload, Base64.NO_WRAP)
            val iv = data.copyOfRange(0, GCM_IV_LEN)
            val ct = data.copyOfRange(GCM_IV_LEN, data.size)
            val cipher = Cipher.getInstance(TRANSFORMATION).apply {
                init(Cipher.DECRYPT_MODE, getOrCreateKey(), GCMParameterSpec(GCM_TAG_BITS, iv))
            }
            String(cipher.doFinal(ct), Charsets.UTF_8)
        }.getOrDefault("")
    }

    /* ---------------- Key management ---------------- */

    private fun getOrCreateKey(): SecretKey {
        val ks = KeyStore.getInstance(KEYSTORE).apply { load(null) }
        (ks.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }

        val kg = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE)
        kg.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
        )
        return kg.generateKey()
    }

    private companion object {
        const val KEYSTORE = "AndroidKeyStore"
        const val KEY_ALIAS = "noor_e_islam_user_key_v1"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val GCM_IV_LEN = 12
        const val GCM_TAG_BITS = 128
    }
}

