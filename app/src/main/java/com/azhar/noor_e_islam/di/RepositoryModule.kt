package com.azhar.noor_e_islam.di

import com.azhar.noor_e_islam.data.repository.*
import com.azhar.noor_e_islam.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton abstract fun authRepo(impl: AuthRepositoryImpl): AuthRepository
    @Binds @Singleton abstract fun quranRepo(impl: QuranRepositoryImpl): QuranRepository
    @Binds @Singleton abstract fun bookmarkRepo(impl: BookmarkRepositoryImpl): BookmarkRepository
    @Binds @Singleton abstract fun noteRepo(impl: NoteRepositoryImpl): NoteRepository
    @Binds @Singleton abstract fun habitRepo(impl: HabitRepositoryImpl): HabitRepository
    @Binds @Singleton abstract fun duaRepo(impl: DuaRepositoryImpl): DuaRepository
    @Binds @Singleton abstract fun storyRepo(impl: StoryRepositoryImpl): StoryRepository
    @Binds @Singleton abstract fun calendarRepo(impl: CalendarRepositoryImpl): CalendarRepository
    @Binds @Singleton abstract fun incidentRepo(impl: IncidentRepositoryImpl): IncidentRepository
    @Binds @Singleton abstract fun progressRepo(impl: ProgressRepositoryImpl): ProgressRepository
    @Binds @Singleton abstract fun userPrefsRepo(impl: UserPrefsRepositoryImpl): UserPrefsRepository
}

