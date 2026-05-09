package com.azhar.noor_e_islam.domain.model

data class User(
    val uid: String,
    val name: String?,
    val email: String?,
    val photoUrl: String? = null,
    val isAnonymous: Boolean = false,
    val emailVerified: Boolean = false,
)

data class Surah(
    val number: Int,
    val name: String,           // Arabic name
    val englishName: String,
    val englishMeaning: String,
    val ayahCount: Int,
    val revelationType: RevelationType,
)

enum class RevelationType { MAKKI, MADANI }

data class Ayah(
    val surahNumber: Int,
    val number: Int,            // ayah number within surah
    val globalNumber: Int = 0,  // 1..6236 across the whole Quran (used for audio fallback)
    val arabic: String,
    val translation: String,
    val transliteration: String? = null,
    val audioUrl: String? = null,
    val juz: Int = 1,
) {
    /** Always-available audio URL: prefers cached [audioUrl], falls back to alquran CDN by [globalNumber]. */
    val effectiveAudioUrl: String?
        get() = audioUrl ?: globalNumber.takeIf { it in 1..6236 }
            ?.let { "https://cdn.islamic.network/quran/audio/128/ar.alafasy/$it.mp3" }
}

data class Bookmark(
    val id: String,
    val type: BookmarkType,
    val refId: String,
    val title: String,
    val subtitle: String?,
    val createdAt: Long = System.currentTimeMillis(),
)
enum class BookmarkType { AYAH, HADITH, DUA, STORY }

data class Note(
    val id: String,
    val title: String,
    val body: String,
    val tags: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)

data class Habit(
    val id: String,
    val title: String,
    val emoji: String,
    val targetPerDay: Int = 1,
    val streak: Int = 0,
    val active: Boolean = true,
)

data class HabitLog(
    val id: String,
    val habitId: String,
    val date: String, // yyyy-MM-dd
    val count: Int,
)

data class Dua(
    val id: String,
    val category: String,
    val title: String,
    val arabic: String,
    val transliteration: String,
    val translation: String,
    val reference: String,
    val audioUrl: String? = null,
)

data class Story(
    val id: String,
    val category: StoryCategory,
    val title: String,
    val coverUrl: String?,
    val summary: String,
    val body: String,
    val durationMin: Int = 5,
)
enum class StoryCategory { GHAZWAT, SAHABA, PROPHETS, HEROES }

data class IslamicIncident(
    val id: String,
    val title: String,
    val date: String, // Hijri date string
    val gregorian: String,
    val location: String?,
    val summary: String,
    val body: String,
    val imageUrl: String?,
)

data class HijriDate(
    val day: Int,
    val month: Int,
    val monthName: String,
    val year: Int,
)

data class PrayerTimes(
    val fajr: String, val sunrise: String, val dhuhr: String,
    val asr: String, val maghrib: String, val isha: String,
)

data class ReadingProgress(
    val totalAyahsRead: Int = 0,
    val streakDays: Int = 0,
    val lastReadSurah: Int = 1,
    val lastReadAyah: Int = 1,
    val dailyGoalAyahs: Int = 10,
)

