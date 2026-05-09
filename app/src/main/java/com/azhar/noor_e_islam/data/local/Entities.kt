package com.azhar.noor_e_islam.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "surah")
data class SurahEntity(
    @PrimaryKey val number: Int,
    val name: String,
    val englishName: String,
    val englishMeaning: String,
    val ayahCount: Int,
    val revelationType: String, // MAKKI / MADANI
)

@Entity(tableName = "ayah", primaryKeys = ["surahNumber", "number"])
data class AyahEntity(
    val surahNumber: Int,
    val number: Int,
    /** Global ayah index 1..6236 — used to reconstruct the alquran CDN audio URL when [audioUrl] is null. */
    val globalNumber: Int = 0,
    val arabic: String,
    val translation: String,
    val transliteration: String? = null,
    val audioUrl: String? = null,
    val juz: Int = 1,
)

@Entity(tableName = "bookmark")
data class BookmarkEntity(
    @PrimaryKey val id: String,
    val type: String,
    val refId: String,
    val title: String,
    val subtitle: String?,
    val createdAt: Long,
)

@Entity(tableName = "note")
data class NoteEntity(
    @PrimaryKey val id: String,
    val title: String,
    val body: String,
    val tags: String, // CSV
    val createdAt: Long,
    val updatedAt: Long,
)

@Entity(tableName = "habit")
data class HabitEntity(
    @PrimaryKey val id: String,
    val title: String,
    val emoji: String,
    val targetPerDay: Int,
    val streak: Int,
    val active: Boolean,
)

@Entity(tableName = "habit_log", primaryKeys = ["habitId", "date"])
data class HabitLogEntity(
    val habitId: String,
    val date: String,
    val count: Int,
)

@Entity(tableName = "dua")
data class DuaEntity(
    @PrimaryKey val id: String,
    val category: String,
    val title: String,
    val arabic: String,
    val transliteration: String,
    val translation: String,
    val reference: String,
    val audioUrl: String? = null,
)

@Entity(tableName = "story")
data class StoryEntity(
    @PrimaryKey val id: String,
    val category: String,
    val title: String,
    val coverUrl: String?,
    val summary: String,
    val body: String,
    val durationMin: Int,
)

@Entity(tableName = "reading_progress")
data class ReadingProgressEntity(
    @PrimaryKey val id: Int = 1, // single-row
    val totalAyahsRead: Int,
    val streakDays: Int,
    val lastReadSurah: Int,
    val lastReadAyah: Int,
    val dailyGoalAyahs: Int,
)

@Entity(tableName = "incident")
data class IncidentEntity(
    @PrimaryKey val id: String,
    val title: String,
    val date: String,
    val gregorian: String,
    val location: String?,
    val summary: String,
    val body: String,
    val imageUrl: String?,
)

