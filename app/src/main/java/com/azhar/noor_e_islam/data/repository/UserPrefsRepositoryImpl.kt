package com.azhar.noor_e_islam.data.repository

import com.azhar.noor_e_islam.core.datastore.UserPrefs
import com.azhar.noor_e_islam.core.datastore.UserPreferences
import com.azhar.noor_e_islam.domain.repository.UserPrefsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPrefsRepositoryImpl @Inject constructor(
    private val store: UserPreferences
) : UserPrefsRepository {
    override val prefs: Flow<UserPrefs> = store.prefs
    override suspend fun setOnboardingDone(done: Boolean) = store.setOnboardingDone(done)
    override suspend fun setThemeMode(mode: String) = store.setThemeMode(mode)
    override suspend fun setLocale(locale: String) = store.setLocale(locale)
    override suspend fun setQuranFontScale(scale: Float) = store.setQuranFontScale(scale)
    override suspend fun setHadithFontScale(scale: Float) = store.setHadithFontScale(scale)
}

