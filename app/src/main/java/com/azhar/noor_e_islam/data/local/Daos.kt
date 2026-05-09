package com.azhar.noor_e_islam.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SurahDao {
    @Query("SELECT * FROM surah ORDER BY number ASC")
    fun observeAll(): Flow<List<SurahEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<SurahEntity>)

    @Query("SELECT COUNT(*) FROM surah") suspend fun count(): Int
}

@Dao
interface AyahDao {
    @Query("SELECT * FROM ayah WHERE surahNumber = :surah ORDER BY number ASC")
    fun observeForSurah(surah: Int): Flow<List<AyahEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<AyahEntity>)

    @Query("SELECT * FROM ayah WHERE arabic LIKE '%' || :q || '%' OR translation LIKE '%' || :q || '%' LIMIT 100")
    suspend fun search(q: String): List<AyahEntity>
}

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmark ORDER BY createdAt DESC") fun observeAll(): Flow<List<BookmarkEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(b: BookmarkEntity)
    @Query("DELETE FROM bookmark WHERE id = :id") suspend fun delete(id: String)
}

@Dao
interface NoteDao {
    @Query("SELECT * FROM note ORDER BY updatedAt DESC") fun observeAll(): Flow<List<NoteEntity>>
    @Query("SELECT * FROM note WHERE id = :id") suspend fun get(id: String): NoteEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(n: NoteEntity)
    @Query("DELETE FROM note WHERE id = :id") suspend fun delete(id: String)
}

@Dao
interface HabitDao {
    @Query("SELECT * FROM habit ORDER BY title ASC") fun observeAll(): Flow<List<HabitEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(h: HabitEntity)
    @Query("DELETE FROM habit WHERE id = :id") suspend fun delete(id: String)
    @Query("SELECT * FROM habit_log WHERE habitId = :id ORDER BY date DESC") fun observeLogs(id: String): Flow<List<HabitLogEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertLog(log: HabitLogEntity)
}

@Dao
interface DuaDao {
    @Query("SELECT * FROM dua") fun observeAll(): Flow<List<DuaEntity>>
    @Query("SELECT * FROM dua WHERE category = :c") fun observeByCategory(c: String): Flow<List<DuaEntity>>
    @Query("SELECT * FROM dua WHERE id = :id") suspend fun get(id: String): DuaEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertAll(items: List<DuaEntity>)
}

@Dao
interface StoryDao {
    @Query("SELECT * FROM story") fun observeAll(): Flow<List<StoryEntity>>
    @Query("SELECT * FROM story WHERE category = :c") fun observeByCategory(c: String): Flow<List<StoryEntity>>
    @Query("SELECT * FROM story WHERE id = :id") suspend fun get(id: String): StoryEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertAll(items: List<StoryEntity>)
}

@Dao
interface ProgressDao {
    @Query("SELECT * FROM reading_progress WHERE id = 1") fun observe(): Flow<ReadingProgressEntity?>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(p: ReadingProgressEntity)
}

@Dao
interface IncidentDao {
    @Query("SELECT * FROM incident") fun observeAll(): Flow<List<IncidentEntity>>
    @Query("SELECT * FROM incident WHERE id = :id") suspend fun get(id: String): IncidentEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertAll(items: List<IncidentEntity>)
}

