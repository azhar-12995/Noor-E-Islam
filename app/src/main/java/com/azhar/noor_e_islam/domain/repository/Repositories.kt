package com.azhar.noor_e_islam.domain.repository

import com.azhar.noor_e_islam.core.util.Resource
import com.azhar.noor_e_islam.domain.model.*
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun login(email: String, password: String): Resource<User>
    suspend fun register(name: String, email: String, password: String): Resource<User>
    suspend fun forgotPassword(email: String): Resource<Unit>
    suspend fun signInAnonymously(): Resource<User>
    suspend fun signInWithGoogle(idToken: String): Resource<User>
    suspend fun sendEmailVerification(): Resource<Unit>
    suspend fun logout()
}

interface QuranRepository {
    fun getSurahs(): Flow<Resource<List<Surah>>>
    fun getAyahs(surahNumber: Int): Flow<Resource<List<Ayah>>>
    suspend fun search(query: String): Resource<List<Ayah>>
    suspend fun updateLastRead(surah: Int, ayah: Int)
}

interface BookmarkRepository {
    fun observeAll(): Flow<List<Bookmark>>
    suspend fun add(bookmark: Bookmark)
    suspend fun remove(id: String)
}

interface NoteRepository {
    fun observeAll(): Flow<List<Note>>
    suspend fun upsert(note: Note)
    suspend fun delete(id: String)
    suspend fun get(id: String): Note?
}

interface HabitRepository {
    fun observeAll(): Flow<List<Habit>>
    fun observeLogs(habitId: String): Flow<List<HabitLog>>
    suspend fun add(habit: Habit)
    suspend fun delete(id: String)
    suspend fun log(habitId: String, count: Int = 1)
}

interface DuaRepository {
    fun byCategory(category: String): Flow<Resource<List<Dua>>>
    fun all(): Flow<Resource<List<Dua>>>
    suspend fun get(id: String): Dua?
}

interface StoryRepository {
    fun all(): Flow<Resource<List<Story>>>
    fun byCategory(c: StoryCategory): Flow<Resource<List<Story>>>
    suspend fun get(id: String): Story?
}

interface CalendarRepository {
    suspend fun today(): Resource<HijriDate>
    suspend fun prayerTimes(lat: Double, lng: Double): Resource<PrayerTimes>
}

interface IncidentRepository {
    fun all(): Flow<Resource<List<IslamicIncident>>>
    suspend fun get(id: String): IslamicIncident?
}

interface ProgressRepository {
    fun observe(): Flow<ReadingProgress>
    suspend fun increment(ayahsRead: Int)
}

interface UserPrefsRepository {
    val prefs: Flow<com.azhar.noor_e_islam.core.datastore.UserPrefs>
    suspend fun setOnboardingDone(done: Boolean)
    suspend fun setThemeMode(mode: String)
    suspend fun setLocale(locale: String)
    suspend fun setQuranFontScale(scale: Float)
    suspend fun setHadithFontScale(scale: Float)
}

