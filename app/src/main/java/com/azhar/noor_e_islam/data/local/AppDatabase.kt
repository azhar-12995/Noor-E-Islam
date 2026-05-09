package com.azhar.noor_e_islam.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        SurahEntity::class,
        AyahEntity::class,
        BookmarkEntity::class,
        NoteEntity::class,
        HabitEntity::class,
        HabitLogEntity::class,
        DuaEntity::class,
        StoryEntity::class,
        ReadingProgressEntity::class,
        IncidentEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun surahDao(): SurahDao
    abstract fun ayahDao(): AyahDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun noteDao(): NoteDao
    abstract fun habitDao(): HabitDao
    abstract fun duaDao(): DuaDao
    abstract fun storyDao(): StoryDao
    abstract fun progressDao(): ProgressDao
    abstract fun incidentDao(): IncidentDao
}

