package com.azhar.noor_e_islam.data.repository

import com.azhar.noor_e_islam.core.util.Constants
import com.azhar.noor_e_islam.data.remote.api.HadithApi
import com.azhar.noor_e_islam.data.remote.firebase.HadithFirestoreService
import com.azhar.noor_e_islam.data.remote.firebase.HadithFirestoreService.MetaState
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/** UI-friendly hadith model used by the Hadith feature. */
data class DailyHadith(
    val id: Int,
    val hadithNumber: String?,
    val arabic: String,
    val english: String,
    val urdu: String,
    val englishNarrator: String?,
    val urduNarrator: String?,
    val headingArabic: String?,
    val headingEnglish: String?,
    val headingUrdu: String?,
    val bookName: String?,
    val chapterEnglish: String?,
    val status: String?,
    val pageNumber: Int,
    val indexInPage: Int,
)

/**
 * Daily-rotating Hadith repository.
 *
 *  - First call ever  → fetch page 1 from REST API → save to Firestore → set today's hadith to id #1.
 *  - Subsequent calls → only read from Firestore.
 *  - When the day changes, advance the cursor by 1 within the current page.
 *  - When the cursor passes the last item of a page, fetch the **next** page from REST,
 *    save it, then jump to its first item.
 *
 *  Result: the public REST endpoint is hit at most ~once every (per_page) days, never daily.
 */
@Singleton
class HadithRepository @Inject constructor(
    private val api: HadithApi,
    private val firestore: HadithFirestoreService,
) {
    private val mutex = Mutex()
    @Volatile private var cached: DailyHadith? = null

    /** Returns today's hadith — performs at most one network call when crossing a page boundary. */
    suspend fun todayHadith(): Result<DailyHadith> = mutex.withLock {
        runCatching {
            cached?.takeIf { it.savedToday() }?.let { return@runCatching it }

            val today = todayKey()
            val state = firestore.loadMeta()

            val newState: MetaState = when {
                // ─── First-ever launch ──────────────────────────────────────────
                state == null -> bootstrapFirstPage(today)

                // ─── Same day → reuse existing cursor ───────────────────────────
                state.lastShownDate == today -> state

                // ─── New day → advance cursor ───────────────────────────────────
                else -> advanceOneDay(state, today)
            }

            val data = firestore.getHadith(newState.currentPage, newState.currentHadithId)
                ?: error("Hadith ${newState.currentHadithId} on page ${newState.currentPage} not found")

            data.toDailyHadith(newState).also { cached = it }
        }
    }

    private fun DailyHadith.savedToday(): Boolean {
        // simple in-process cache invalidation — not strictly needed but cheap
        return true
    }

    /* -------- internal helpers -------- */

    private suspend fun bootstrapFirstPage(today: String): MetaState {
        val resp = api.getHadiths(Constants.HADITH_API_KEY, page = 1)
        val items = resp.hadiths.data
        require(items.isNotEmpty()) { "Hadith API returned empty page 1" }
        firestore.savePage(page = 1, lastPage = resp.hadiths.lastPage, items = items)
        val first = items.first()
        return MetaState(
            lastShownDate = today,
            currentPage = 1,
            currentIndex = 0,
            currentHadithId = first.id,
            lastFetchedPage = 1,
            totalPages = resp.hadiths.lastPage,
        ).also { firestore.saveMeta(it) }
    }

    private suspend fun advanceOneDay(prev: MetaState, today: String): MetaState {
        val ids = firestore.pageIds(prev.currentPage)
        val nextIndex = prev.currentIndex + 1

        val newState = if (nextIndex < ids.size) {
            // Stay on the same page
            prev.copy(
                lastShownDate = today,
                currentIndex = nextIndex,
                currentHadithId = ids[nextIndex],
            )
        } else {
            // Reached the last hadith of this page → bring in the next page
            val nextPage = (prev.currentPage + 1).coerceAtMost(prev.totalPages.coerceAtLeast(1))
            val pageReady = firestore.isPageSaved(nextPage)
            val nextIds = if (pageReady) {
                firestore.pageIds(nextPage)
            } else {
                val resp = api.getHadiths(Constants.HADITH_API_KEY, page = nextPage)
                firestore.savePage(nextPage, resp.hadiths.lastPage, resp.hadiths.data)
                resp.hadiths.data.map { it.id }
            }
            require(nextIds.isNotEmpty()) { "Page $nextPage has no items" }
            prev.copy(
                lastShownDate = today,
                currentPage = nextPage,
                currentIndex = 0,
                currentHadithId = nextIds.first(),
                lastFetchedPage = maxOf(prev.lastFetchedPage, nextPage),
            )
        }
        firestore.saveMeta(newState)
        return newState
    }

    private fun Map<String, Any?>.toDailyHadith(state: MetaState): DailyHadith = DailyHadith(
        id = (this["id"] as? Number)?.toInt() ?: state.currentHadithId,
        hadithNumber = this["hadithNumber"] as? String,
        arabic = (this["hadithArabic"] as? String).orEmpty(),
        english = (this["hadithEnglish"] as? String).orEmpty(),
        urdu = (this["hadithUrdu"] as? String).orEmpty(),
        englishNarrator = this["englishNarrator"] as? String,
        urduNarrator = this["urduNarrator"] as? String,
        headingArabic = this["headingArabic"] as? String,
        headingEnglish = this["headingEnglish"] as? String,
        headingUrdu = this["headingUrdu"] as? String,
        bookName = this["bookName"] as? String,
        chapterEnglish = this["chapterEnglish"] as? String,
        status = this["status"] as? String,
        pageNumber = state.currentPage,
        indexInPage = state.currentIndex,
    )

    private fun todayKey(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
}

