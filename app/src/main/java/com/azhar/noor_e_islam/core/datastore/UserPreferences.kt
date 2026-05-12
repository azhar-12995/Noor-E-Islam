package com.azhar.noor_e_islam.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
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
    val quranFontScale: Float = 1f,
    val hadithFontScale: Float = 1f,
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
        val QURAN_FONT      = floatPreferencesKey("quran_font_scale")
        val HADITH_FONT     = floatPreferencesKey("hadith_font_scale")
        val TRANSLATION     = stringPreferencesKey("translation")
        val RECITER         = stringPreferencesKey("reciter")
        val LAST_SURAH      = intPreferencesKey("last_surah")
        val LAST_AYAH       = intPreferencesKey("last_ayah")
        val NOTIFS_SEEN_AT  = longPreferencesKey("notifs_last_seen_at")
        val ANN_SEEN_ID     = stringPreferencesKey("last_seen_announcement_id")
        val PRAYER_LAT      = stringPreferencesKey("prayer_lat")  // stored as String for DataStore (no Double key)
        val PRAYER_LNG      = stringPreferencesKey("prayer_lng")
        val HADITH_NOTIF_DATE = stringPreferencesKey("hadith_notif_last_date")
    }

    val prefs: Flow<UserPrefs> = context.userDataStore.data.map { p ->
        UserPrefs(
            onboardingDone   = p[Keys.ONBOARDING_DONE] ?: false,
            themeMode        = p[Keys.THEME_MODE] ?: "system",
            locale           = p[Keys.LOCALE] ?: "en",
            fontScale        = p[Keys.FONT_SCALE] ?: 1f,
            quranFontScale   = p[Keys.QURAN_FONT] ?: 1f,
            hadithFontScale  = p[Keys.HADITH_FONT] ?: 1f,
            translation      = p[Keys.TRANSLATION] ?: "en.sahih",
            reciter          = p[Keys.RECITER] ?: "ar.alafasy",
            lastReadSurah    = p[Keys.LAST_SURAH] ?: 1,
            lastReadAyah     = p[Keys.LAST_AYAH] ?: 1,
        )
    }

    suspend fun setOnboardingDone(done: Boolean)        { context.userDataStore.edit { it[Keys.ONBOARDING_DONE] = done } }
    suspend fun setThemeMode(mode: String)              { context.userDataStore.edit { it[Keys.THEME_MODE] = mode } }
    suspend fun setLocale(locale: String)               { context.userDataStore.edit { it[Keys.LOCALE] = locale } }
    suspend fun setFontScale(scale: Float)              { context.userDataStore.edit { it[Keys.FONT_SCALE] = scale } }
    suspend fun setQuranFontScale(scale: Float)         { context.userDataStore.edit { it[Keys.QURAN_FONT] = scale } }
    suspend fun setHadithFontScale(scale: Float)        { context.userDataStore.edit { it[Keys.HADITH_FONT] = scale } }
    suspend fun setTranslation(value: String)           { context.userDataStore.edit { it[Keys.TRANSLATION] = value } }
    suspend fun setReciter(value: String)               { context.userDataStore.edit { it[Keys.RECITER] = value } }
    suspend fun setLastRead(surah: Int, ayah: Int)      { context.userDataStore.edit { it[Keys.LAST_SURAH] = surah; it[Keys.LAST_AYAH] = ayah } }

    val notificationsLastSeenAt: Flow<Long> =
        context.userDataStore.data.map { it[Keys.NOTIFS_SEEN_AT] ?: 0L }
    suspend fun markNotificationsSeen() {
        context.userDataStore.edit { it[Keys.NOTIFS_SEEN_AT] = System.currentTimeMillis() }
    }

    val lastSeenAnnouncementId: Flow<String> =
        context.userDataStore.data.map { it[Keys.ANN_SEEN_ID].orEmpty() }
    suspend fun markAnnouncementSeen(id: String) {
        context.userDataStore.edit { it[Keys.ANN_SEEN_ID] = id }
    }

    /** Last known (lat,lng) used by background prayer-alarm scheduling when
     *  the app is killed and we can't query FusedLocation any more. */
    val cachedLocation: Flow<Pair<Double, Double>?> =
        context.userDataStore.data.map { p ->
            val lat = p[Keys.PRAYER_LAT]?.toDoubleOrNull()
            val lng = p[Keys.PRAYER_LNG]?.toDoubleOrNull()
            if (lat != null && lng != null) lat to lng else null
        }
    suspend fun saveLocation(lat: Double, lng: Double) {
        context.userDataStore.edit {
            it[Keys.PRAYER_LAT] = lat.toString()
            it[Keys.PRAYER_LNG] = lng.toString()
        }
    }

    /** YYYY-MM-DD of the last day a "Hadith of the Day" notification was posted.
     *  Used by [com.azhar.noor_e_islam.core.notifications.HadithDailyWorker] to
     *  guarantee at most one notification per calendar day, even if the worker
     *  is re-triggered (e.g. after a reboot mid-day). */
    val hadithNotifDate: Flow<String> =
        context.userDataStore.data.map { it[Keys.HADITH_NOTIF_DATE].orEmpty() }
    suspend fun setHadithNotifDate(date: String) {
        context.userDataStore.edit { it[Keys.HADITH_NOTIF_DATE] = date }
    }
}
