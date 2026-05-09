package com.azhar.noor_e_islam.data.mapper

import com.azhar.noor_e_islam.data.local.*
import com.azhar.noor_e_islam.domain.model.*

fun SurahEntity.toDomain() = Surah(
    number = number,
    name = name,
    englishName = englishName,
    englishMeaning = englishMeaning,
    ayahCount = ayahCount,
    revelationType = runCatching { RevelationType.valueOf(revelationType) }.getOrDefault(RevelationType.MAKKI)
)
fun Surah.toEntity() = SurahEntity(number, name, englishName, englishMeaning, ayahCount, revelationType.name)

fun AyahEntity.toDomain() = Ayah(surahNumber, number, globalNumber, arabic, translation, transliteration, audioUrl, juz)
fun Ayah.toEntity() = AyahEntity(surahNumber, number, globalNumber, arabic, translation, transliteration, audioUrl, juz)

fun BookmarkEntity.toDomain() = Bookmark(id, runCatching { BookmarkType.valueOf(type) }.getOrDefault(BookmarkType.AYAH), refId, title, subtitle, createdAt)
fun Bookmark.toEntity() = BookmarkEntity(id, type.name, refId, title, subtitle, createdAt)

fun NoteEntity.toDomain() = Note(id, title, body, tags.split(",").filter { it.isNotBlank() }, createdAt, updatedAt)
fun Note.toEntity() = NoteEntity(id, title, body, tags.joinToString(","), createdAt, updatedAt)

fun HabitEntity.toDomain() = Habit(id, title, emoji, targetPerDay, streak, active)
fun Habit.toEntity() = HabitEntity(id, title, emoji, targetPerDay, streak, active)

fun HabitLogEntity.toDomain() = HabitLog("$habitId-$date", habitId, date, count)
fun HabitLog.toEntity() = HabitLogEntity(habitId, date, count)

fun DuaEntity.toDomain() = Dua(id, category, title, arabic, transliteration, translation, reference, audioUrl)
fun Dua.toEntity() = DuaEntity(id, category, title, arabic, transliteration, translation, reference, audioUrl)

fun StoryEntity.toDomain() = Story(id, runCatching { StoryCategory.valueOf(category) }.getOrDefault(StoryCategory.SAHABA), title, coverUrl, summary, body, durationMin)
fun Story.toEntity() = StoryEntity(id, category.name, title, coverUrl, summary, body, durationMin)

fun ReadingProgressEntity.toDomain() = ReadingProgress(totalAyahsRead, streakDays, lastReadSurah, lastReadAyah, dailyGoalAyahs)
fun ReadingProgress.toEntity() = ReadingProgressEntity(1, totalAyahsRead, streakDays, lastReadSurah, lastReadAyah, dailyGoalAyahs)

fun IncidentEntity.toDomain() = IslamicIncident(id, title, date, gregorian, location, summary, body, imageUrl)
fun IslamicIncident.toEntity() = IncidentEntity(id, title, date, gregorian, location, summary, body, imageUrl)

