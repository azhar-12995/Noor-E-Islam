package com.azhar.noor_e_islam.data.repository

import com.azhar.noor_e_islam.core.datastore.UserPreferences
import com.azhar.noor_e_islam.core.util.Resource
import com.azhar.noor_e_islam.core.util.safeCall
import com.azhar.noor_e_islam.data.local.AyahDao
import com.azhar.noor_e_islam.data.local.AyahEntity
import com.azhar.noor_e_islam.data.local.SurahDao
import com.azhar.noor_e_islam.data.local.SurahEntity
import com.azhar.noor_e_islam.data.mapper.toDomain
import com.azhar.noor_e_islam.data.remote.api.BulkSurahDto
import com.azhar.noor_e_islam.data.remote.api.QuranApi
import com.azhar.noor_e_islam.data.remote.firebase.QuranFirestoreService
import com.azhar.noor_e_islam.domain.model.Ayah
import com.azhar.noor_e_islam.domain.model.Surah
import com.azhar.noor_e_islam.domain.repository.QuranRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Offline-first Quran repository with a 3-tier loading chain:
 *
 *   1. Room (local cache)         — fastest, works offline.
 *   2. Firestore (cloud cache)    — populated by the first user, free for everyone after.
 *   3. REST API (alquran.cloud)   — network fetch as last resort; results are persisted to
 *                                   both Firestore (best-effort) and Room.
 *
 * Once Room is populated (114 surahs) the network is never touched again.
 */
@Singleton
class QuranRepositoryImpl @Inject constructor(
    private val api: QuranApi,
    private val firestoreCache: QuranFirestoreService,
    private val surahDao: SurahDao,
    private val ayahDao: AyahDao,
    private val prefs: UserPreferences,
) : QuranRepository {

    private val populateMutex = Mutex()

    private suspend fun ensurePopulated() = populateMutex.withLock {
        if (surahDao.count() >= TOTAL_SURAHS) return@withLock

        // 2. Try Firestore
        runCatching {
            if (firestoreCache.isPopulated()) {
                Timber.i("Quran: hydrating from Firestore cache")
                val (surahs, ayahs) = firestoreCache.fetchAll()
                if (surahs.size == TOTAL_SURAHS && ayahs.isNotEmpty()) {
                    surahDao.upsertAll(surahs)
                    ayahDao.upsertAll(ayahs)
                    return@withLock
                }
            }
        }.onFailure { Timber.w(it, "Quran: Firestore read failed; falling back to REST") }

        // 3. REST: fetch Arabic+audio and English translation, then merge.
        Timber.i("Quran: fetching from REST (alquran.cloud)")
        val arabic = api.fullQuran(EDITION_ARABIC).data.surahs
        val english = runCatching { api.fullQuran(EDITION_ENGLISH).data.surahs }.getOrDefault(emptyList())
        val englishMap = english.associateBy { it.number }

        val surahEntities = arabic.map { it.toSurahEntity() }
        val ayahEntities = arabic.flatMap { surahDto ->
            val translations = englishMap[surahDto.number]?.ayahs?.associateBy { it.numberInSurah }.orEmpty()
            surahDto.ayahs.map { a ->
                AyahEntity(
                    surahNumber = surahDto.number,
                    number = a.numberInSurah,
                    globalNumber = a.number, // 1..6236
                    arabic = a.text,
                    translation = translations[a.numberInSurah]?.text.orEmpty(),
                    transliteration = null,
                    // Always store an audio URL — fall back to the CDN pattern if API omitted it.
                    audioUrl = a.audio
                        ?: "https://cdn.islamic.network/quran/audio/128/ar.alafasy/${a.number}.mp3",
                    juz = a.juz,
                )
            }
        }

        surahDao.upsertAll(surahEntities)
        ayahDao.upsertAll(ayahEntities)

        // Best-effort: publish to Firestore for other users.
        runCatching { firestoreCache.publish(surahEntities, ayahEntities) }
            .onFailure { Timber.w(it, "Quran: Firestore publish failed (likely permissions); local cache OK.") }
    }

    override fun getSurahs(): Flow<Resource<List<Surah>>> = flow {
        emit(Resource.Loading)
        runCatching { ensurePopulated() }
            .onFailure { emit(Resource.Error(it.localizedMessage ?: "Failed to load Quran", it)); return@flow }
        surahDao.observeAll().collect { entities ->
            emit(Resource.Success(entities.map { it.toDomain() }))
        }
    }

    override fun getAyahs(surahNumber: Int): Flow<Resource<List<Ayah>>> = flow {
        emit(Resource.Loading)
        runCatching { ensurePopulated() }
            .onFailure { emit(Resource.Error(it.localizedMessage ?: "Failed to load Quran", it)); return@flow }
        ayahDao.observeForSurah(surahNumber).collect { entities ->
            emit(Resource.Success(entities.map { it.toDomain() }))
        }
    }

    override suspend fun search(query: String): Resource<List<Ayah>> = safeCall {
        ayahDao.search(query).map { it.toDomain() }
    }

    override suspend fun updateLastRead(surah: Int, ayah: Int) {
        prefs.setLastRead(surah, ayah)
    }

    private fun BulkSurahDto.toSurahEntity() = SurahEntity(
        number = number,
        name = name,
        englishName = englishName,
        englishMeaning = englishNameTranslation,
        ayahCount = ayahs.size,
        revelationType = if (revelationType.equals("Meccan", true)) "MAKKI" else "MADANI",
    )

    private companion object {
        const val EDITION_ARABIC = "ar.alafasy"
        const val EDITION_ENGLISH = "en.sahih"
        const val TOTAL_SURAHS = 114
    }
}
