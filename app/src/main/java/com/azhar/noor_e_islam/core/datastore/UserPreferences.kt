package com.azhar.noor_e_islam.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.userDataStore by preferencesDataStore(name = "noor_user_prefs")

/** Domain model exposed by [UserPreferences]. */
data class UserPrefs(
    val onboardingDone: Boolean = false,
    val themeMode: String = "system", // system | light | dark
    val locale: String = "en",
    val fontScale: Float = 1f,
    val translation: String = "en.sahih",
    val reciter: String = "ar.alafasy",
    val lastReadSurah: Int = 1,
    val lastReadAyah: Int = 1,
)

@Singleton
class UserPreferences @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: Context,
) {
    private object Keys {
        val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
        val THEME_MODE      = stringPreferencesKey("theme_mode")
        val LOCALE          = stringPreferencesKey("locale")
        val FONT_SCALE      = floatPreferencesKey("font_scale")
        val TRANSLATION     = stringPreferencesKey("translation")
        val RECITER         = stringPreferencesKey("reciter")
        val LAST_SURAH      = intPreferencesKey("last_surah")
        val LAST_AYAH       = intPreferencesKey("last_ayah")
    }

    val prefs: Flow<UserPrefs> = context.userDataStore.data.map { p ->
        UserPrefs(
            onboardingDone = p[Keys.ONBOARDING_DONE] ?: false,
            themeMode      = p[Keys.THEME_MODE] ?: "system",
            locale         = p[Keys.LOCALE] ?: "en",
            fontScale      = p[Keys.FONT_SCALE] ?: 1f,
            translation    = p[Keys.TRANSLATION] ?: "en.sahih",
            reciter        = p[Keys.RECITER] ?: "ar.alafasy",
            lastReadSurah  = p[Keys.LAST_SURAH] ?: 1,
            lastReadAyah   = p[Keys.LAST_AYAH] ?: 1,
        )
    }

    suspend fun setOnboardingDone(done: Boolean)        { context.userDataStore.edit { it[Keys.ONBOARDING_DONE] = done } }
    suspend fun setThemeMode(mode: String)              { context.userDataStore.edit { it[Keys.THEME_MODE] = mode } }
    suspend fun setLocale(locale: String)               { context.userDataStore.edit { it[Keys.LOCALE] = locale } }
    suspend fun setFontScale(scale: Float)              { context.userDataStore.edit { it[Keys.FONT_SCALE] = scale } }
    suspend fun setTranslation(value: String)           { context.userDataStore.edit { it[Keys.TRANSLATION] = value } }
    suspend fun setReciter(value: String)               { context.userDataStore.edit { it[Keys.RECITER] = value } }
    suspend fun setLastRead(surah: Int, ayah: Int)      { context.userDataStore.edit { it[Keys.LAST_SURAH] = surah; it[Keys.LAST_AYAH] = ayah } }
}

