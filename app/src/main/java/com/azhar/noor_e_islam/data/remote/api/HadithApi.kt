package com.azhar.noor_e_islam.data.remote.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Hadith API — https://hadithapi.com
 *
 * Sample response:
 * GET /api/hadiths?apiKey=...&page=1
 * {
 *   "status": 200,
 *   "message": "Hadiths has been found.",
 *   "hadiths": {
 *     "current_page": 1,
 *     "data": [ { hadith ... }, ... ],
 *     "last_page": 1619,
 *     "next_page_url": "https://hadithapi.com/api/hadiths?page=2",
 *     "prev_page_url": null,
 *     "per_page": 25,
 *     "total": 40465
 *   }
 * }
 */
interface HadithApi {
    @GET("hadiths")
    suspend fun getHadiths(
        @Query("apiKey") apiKey: String,
        @Query("page") page: Int,
    ): HadithApiResponse
}

@JsonClass(generateAdapter = true)
data class HadithApiResponse(
    val status: Int,
    val message: String?,
    val hadiths: HadithPageDto,
)

@JsonClass(generateAdapter = true)
data class HadithPageDto(
    @Json(name = "current_page") val currentPage: Int,
    val data: List<HadithDto>,
    @Json(name = "last_page") val lastPage: Int,
    @Json(name = "next_page_url") val nextPageUrl: String?,
    @Json(name = "prev_page_url") val prevPageUrl: String?,
    @Json(name = "per_page") val perPage: Int,
    val total: Int,
)

@JsonClass(generateAdapter = true)
data class HadithDto(
    val id: Int,
    val hadithNumber: String?,
    val englishNarrator: String?,
    val hadithEnglish: String?,
    val hadithUrdu: String?,
    val urduNarrator: String?,
    val hadithArabic: String?,
    val headingArabic: String?,
    val headingUrdu: String?,
    val headingEnglish: String?,
    val chapterId: String?,
    val bookSlug: String?,
    val volume: String?,
    val status: String?,
    val book: HadithBookDto? = null,
    val chapter: HadithChapterDto? = null,
)

@JsonClass(generateAdapter = true)
data class HadithBookDto(
    val id: Int?,
    val bookName: String?,
    val writerName: String?,
    val bookSlug: String?,
)

@JsonClass(generateAdapter = true)
data class HadithChapterDto(
    val id: Int?,
    val chapterNumber: String?,
    val chapterEnglish: String?,
    val chapterUrdu: String?,
    val chapterArabic: String?,
    val bookSlug: String?,
)

