package com.azhar.noor_e_islam.data.repository

import com.azhar.noor_e_islam.data.local.BookmarkDao
import com.azhar.noor_e_islam.data.local.HabitDao
import com.azhar.noor_e_islam.data.local.HabitLogEntity
import com.azhar.noor_e_islam.data.local.NoteDao
import com.azhar.noor_e_islam.data.mapper.toDomain
import com.azhar.noor_e_islam.data.mapper.toEntity
import com.azhar.noor_e_islam.data.remote.firebase.UserDataFirestoreService
import com.azhar.noor_e_islam.domain.model.Bookmark
import com.azhar.noor_e_islam.domain.model.Habit
import com.azhar.noor_e_islam.domain.model.HabitLog
import com.azhar.noor_e_islam.domain.model.Note
import com.azhar.noor_e_islam.domain.repository.BookmarkRepository
import com.azhar.noor_e_islam.domain.repository.HabitRepository
import com.azhar.noor_e_islam.domain.repository.NoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookmarkRepositoryImpl @Inject constructor(
    private val dao: BookmarkDao,
    private val cloud: UserDataFirestoreService,
) : BookmarkRepository {
    private val syncScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    override fun observeAll(): Flow<List<Bookmark>> = dao.observeAll().map { it.map { e -> e.toDomain() } }
    override suspend fun add(bookmark: Bookmark) {
        dao.insert(bookmark.toEntity())
        syncScope.launch { cloud.pushBookmark(bookmark) }   // fire-and-forget cloud sync
    }
    override suspend fun remove(id: String) {
        dao.delete(id)
        syncScope.launch { cloud.deleteBookmark(id) }
    }
}

@Singleton
class NoteRepositoryImpl @Inject constructor(private val dao: NoteDao) : NoteRepository {
    override fun observeAll(): Flow<List<Note>> = dao.observeAll().map { it.map { e -> e.toDomain() } }
    override suspend fun upsert(note: Note) { dao.upsert(note.toEntity()) }
    override suspend fun delete(id: String) { dao.delete(id) }
    override suspend fun get(id: String): Note? = dao.get(id)?.toDomain()
}

@Singleton
class HabitRepositoryImpl @Inject constructor(private val dao: HabitDao) : HabitRepository {
    override fun observeAll(): Flow<List<Habit>> = dao.observeAll().map { it.map { e -> e.toDomain() } }
    override fun observeLogs(habitId: String): Flow<List<HabitLog>> =
        dao.observeLogs(habitId).map { it.map { e -> e.toDomain() } }
    override suspend fun add(habit: Habit) { dao.insert(habit.toEntity()) }
    override suspend fun delete(id: String) { dao.delete(id) }
    override suspend fun log(habitId: String, count: Int) {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        dao.upsertLog(HabitLogEntity(habitId, date, count))
    }
}
