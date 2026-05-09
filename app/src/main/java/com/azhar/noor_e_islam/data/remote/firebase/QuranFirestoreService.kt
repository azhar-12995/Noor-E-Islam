package com.azhar.noor_e_islam.data.remote.firebase

import com.azhar.noor_e_islam.data.local.AyahEntity
import com.azhar.noor_e_islam.data.local.SurahEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Stores the Quran corpus in Firestore so subsequent users don't need
 * to call the public REST API again.
 *
 * Layout:
 *   /quran_meta/info                          — { populated: true, totalSurahs: 114, version: 1 }
 *   /surahs/{number}                          — surah metadata (name, englishName, …)
 *   /surahs/{number}/ayahs/{numberInSurah}    — ayah doc (arabic, translation, audioUrl, juz)
 */
@Singleton
class QuranFirestoreService @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    private val meta     get() = firestore.collection("quran_meta").document("info")
    private val surahCol get() = firestore.collection("surahs")

    /** Returns true if the Quran corpus is already in Firestore at the CURRENT version. */
    suspend fun isPopulated(): Boolean = try {
        val snap = meta.get().await()
        val populated = snap.exists() && (snap.getBoolean("populated") == true)
        val version = (snap.getLong("version") ?: 0).toInt()
        populated && version >= CORPUS_VERSION
    } catch (_: Throwable) { false }

    /** Read all 114 surahs + their ayahs from Firestore. */
    suspend fun fetchAll(): Pair<List<SurahEntity>, List<AyahEntity>> {
        val surahs = mutableListOf<SurahEntity>()
        val ayahs  = mutableListOf<AyahEntity>()
        val docs = surahCol.get().await().documents
        for (doc in docs) {
            val number = (doc.getLong("number") ?: continue).toInt()
            surahs += SurahEntity(
                number = number,
                name = doc.getString("name") ?: "",
                englishName = doc.getString("englishName") ?: "",
                englishMeaning = doc.getString("englishMeaning") ?: "",
                ayahCount = (doc.getLong("ayahCount") ?: 0).toInt(),
                revelationType = doc.getString("revelationType") ?: "MAKKI",
            )
            val ayahDocs = surahCol.document(number.toString())
                .collection("ayahs").get().await().documents
            for (a in ayahDocs) {
                val ayahNum = (a.getLong("number") ?: 0).toInt()
                val global  = (a.getLong("globalNumber") ?: 0).toInt()
                ayahs += AyahEntity(
                    surahNumber = number,
                    number = ayahNum,
                    globalNumber = global,
                    arabic = a.getString("arabic") ?: "",
                    translation = a.getString("translation") ?: "",
                    transliteration = a.getString("transliteration"),
                    // Reconstruct audio URL from globalNumber if the document has none.
                    audioUrl = a.getString("audioUrl")
                        ?: global.takeIf { it in 1..6236 }
                            ?.let { "https://cdn.islamic.network/quran/audio/128/ar.alafasy/$it.mp3" },
                    juz = (a.getLong("juz") ?: 1).toInt(),
                )
            }
        }
        return surahs to ayahs
    }

    /**
     * Push the full Quran to Firestore.
     * Uses batched writes (max 500 ops per batch) for efficiency.
     * Best-effort: caller must catch failures (e.g. unauthenticated, no quota).
     */
    suspend fun publish(surahs: List<SurahEntity>, ayahs: List<AyahEntity>) {
        // 1. Surah docs
        var batch = firestore.batch()
        var ops = 0
        for (s in surahs) {
            val ref = surahCol.document(s.number.toString())
            batch.set(ref, mapOf(
                "number" to s.number,
                "name" to s.name,
                "englishName" to s.englishName,
                "englishMeaning" to s.englishMeaning,
                "ayahCount" to s.ayahCount,
                "revelationType" to s.revelationType,
            ))
            if (++ops >= 450) { batch.commit().await(); batch = firestore.batch(); ops = 0 }
        }
        if (ops > 0) { batch.commit().await(); batch = firestore.batch(); ops = 0 }

        // 2. Ayah docs grouped by surah
        val grouped = ayahs.groupBy { it.surahNumber }
        for ((surahNo, list) in grouped) {
            val ayahCol = surahCol.document(surahNo.toString()).collection("ayahs")
            for (a in list) {
                val ref = ayahCol.document(a.number.toString())
                batch.set(ref, mapOf(
                    "number" to a.number,
                    "globalNumber" to a.globalNumber,
                    "arabic" to a.arabic,
                    "translation" to a.translation,
                    "transliteration" to a.transliteration,
                    "audioUrl" to a.audioUrl,
                    "juz" to a.juz,
                ))
                if (++ops >= 450) { batch.commit().await(); batch = firestore.batch(); ops = 0 }
            }
        }
        if (ops > 0) batch.commit().await()

        // 3. Mark as populated
        meta.set(mapOf(
            "populated" to true,
            "totalSurahs" to surahs.size,
            "version" to CORPUS_VERSION,
            "updatedAt" to System.currentTimeMillis(),
        )).await()
    }

    private companion object {
        /** Bump this whenever the schema or seed data shape changes (e.g. add globalNumber → v2). */
        const val CORPUS_VERSION = 2
    }
}

