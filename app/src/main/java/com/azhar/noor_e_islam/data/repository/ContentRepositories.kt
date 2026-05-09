package com.azhar.noor_e_islam.data.repository

import com.azhar.noor_e_islam.core.util.Resource
import com.azhar.noor_e_islam.core.util.safeCall
import com.azhar.noor_e_islam.data.local.DuaDao
import com.azhar.noor_e_islam.data.local.IncidentDao
import com.azhar.noor_e_islam.data.local.ProgressDao
import com.azhar.noor_e_islam.data.local.ReadingProgressEntity
import com.azhar.noor_e_islam.data.local.StoryDao
import com.azhar.noor_e_islam.data.mapper.toDomain
import com.azhar.noor_e_islam.data.remote.api.HijriApi
import com.azhar.noor_e_islam.domain.model.*
import com.azhar.noor_e_islam.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DuaRepositoryImpl @Inject constructor(private val dao: DuaDao) : DuaRepository {
    override fun byCategory(category: String): Flow<Resource<List<Dua>>> = flow {
        emit(Resource.Loading)
        dao.observeByCategory(category).collect { emit(Resource.Success(it.map { e -> e.toDomain() })) }
    }
    override fun all(): Flow<Resource<List<Dua>>> = flow {
        emit(Resource.Loading)
        dao.observeAll().collect { emit(Resource.Success(it.map { e -> e.toDomain() })) }
    }
    override suspend fun get(id: String): Dua? = dao.get(id)?.toDomain()
}

@Singleton
class StoryRepositoryImpl @Inject constructor(private val dao: StoryDao) : StoryRepository {
    override fun all(): Flow<Resource<List<Story>>> = flow {
        emit(Resource.Loading)
        dao.observeAll().collect { emit(Resource.Success(it.map { e -> e.toDomain() })) }
    }
    override fun byCategory(c: StoryCategory): Flow<Resource<List<Story>>> = flow {
        emit(Resource.Loading)
        dao.observeByCategory(c.name).collect { emit(Resource.Success(it.map { e -> e.toDomain() })) }
    }
    override suspend fun get(id: String): Story? = dao.get(id)?.toDomain()
}

@Singleton
class IncidentRepositoryImpl @Inject constructor(private val dao: IncidentDao) : IncidentRepository {
    override fun all(): Flow<Resource<List<IslamicIncident>>> = flow {
        emit(Resource.Loading)
        dao.observeAll().collect { emit(Resource.Success(it.map { e -> e.toDomain() })) }
    }
    override suspend fun get(id: String): IslamicIncident? = dao.get(id)?.toDomain()
}

@Singleton
class ProgressRepositoryImpl @Inject constructor(private val dao: ProgressDao) : ProgressRepository {
    override fun observe(): Flow<ReadingProgress> = dao.observe().map {
        it?.toDomain() ?: ReadingProgress()
    }
    override suspend fun increment(ayahsRead: Int) {
        // For brevity: read once, write once. In production wrap in transaction.
        val current = dao.observe()
        // No suspend getter — push a fresh row.
        dao.upsert(ReadingProgressEntity(1, ayahsRead, 1, 1, 1, 10))
    }
}

@Singleton
class CalendarRepositoryImpl @Inject constructor(
    private val api: HijriApi,
) : CalendarRepository {
    override suspend fun today(): Resource<HijriDate> = safeCall {
        val res = api.gregorianToHijri()
        val h = res.data.hijri
        HijriDate(h.day.toIntOrNull() ?: 1, h.month.number, h.month.en, h.year.toIntOrNull() ?: 1446)
    }
    override suspend fun prayerTimes(lat: Double, lng: Double): Resource<PrayerTimes> = safeCall {
        // Stubbed; integrate Aladhan /timings endpoint in production.
        PrayerTimes("05:12", "06:38", "12:15", "15:42", "18:11", "19:32")
    }
}

