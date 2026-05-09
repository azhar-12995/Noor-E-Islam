package com.azhar.noor_e_islam.data.remote.api

import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import retrofit2.http.Path

/* --- Quran (alquran.cloud) ---
 * Bulk endpoint: https://api.alquran.cloud/v1/quran/{edition}
 * Returns the entire Quran for a single edition in one response.
 * Edition examples:
 *   "ar.alafasy"   → Arabic uthmani text + Mishary Alafasy audio per ayah
 *   "en.sahih"     → English (Saheeh International) translation, no audio
 *   "ur.junagarhi" → Urdu translation, no audio
 */
interface QuranApi {
    /** Bulk fetch — entire Quran for the given edition. */
    @GET("quran/{edition}")
    suspend fun fullQuran(
        @Path("edition") edition: String
    ): QuranBulkResponse
}

@JsonClass(generateAdapter = true)
data class QuranBulkResponse(
    val code: Int,
    val status: String,
    val data: QuranBulkData,
)

@JsonClass(generateAdapter = true)
data class QuranBulkData(
    val surahs: List<BulkSurahDto>,
    val edition: EditionDto,
)

@JsonClass(generateAdapter = true)
data class BulkSurahDto(
    val number: Int,
    val name: String,
    val englishName: String,
    val englishNameTranslation: String,
    val revelationType: String, // "Meccan" | "Medinan"
    val ayahs: List<BulkAyahDto>,
)

@JsonClass(generateAdapter = true)
data class BulkAyahDto(
    val number: Int,
    val text: String,
    val numberInSurah: Int,
    val juz: Int = 1,
    val audio: String? = null,
    val audioSecondary: List<String>? = null,
)

@JsonClass(generateAdapter = true)
data class EditionDto(
    val identifier: String,
    val language: String,
    val name: String,
    val englishName: String,
    val format: String,
    val type: String,
)

/* --- Aladhan calendar / prayer times --- */
interface HijriApi {
    @GET("gToH")
    suspend fun gregorianToHijri(): HijriDateResponse
}

@JsonClass(generateAdapter = true)
data class HijriDateResponse(val code: Int, val data: HijriDataDto)

@JsonClass(generateAdapter = true)
data class HijriDataDto(val hijri: HijriDto)

@JsonClass(generateAdapter = true)
data class HijriDto(
    val day: String,
    val month: HijriMonthDto,
    val year: String,
)

@JsonClass(generateAdapter = true)
data class HijriMonthDto(val number: Int, val en: String, val ar: String)

