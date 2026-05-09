package com.azhar.noor_e_islam.data.remote.firebase

import com.azhar.noor_e_islam.data.remote.api.HadithDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firestore layout for hadith caching:
 *
 *  hadith_pages/{pageNumber}                       — page meta
 *      ├─ page         : Int
 *      ├─ lastPage     : Int          (overall API last page)
 *      ├─ count        : Int          (items in this page)
 *      ├─ savedAt      : Long         (epoch ms)
 *      └─ ids          : List<Int>    (hadith ids in API order)
 *
 *  hadith_pages/{pageNumber}/items/{hadithId}      — individual hadith doc
 *
 *  hadith_meta/state                               — daily-rotation cursor
 *      ├─ lastShownDate     : String (yyyy-MM-dd)
 *      ├─ currentPage       : Int
 *      ├─ currentIndex      : Int
 *      ├─ currentHadithId   : Int
 *      ├─ lastFetchedPage   : Int
 *      └─ totalPages        : Int
 */
@Singleton
class HadithFirestoreService @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    private val pages get() = firestore.collection(COL_PAGES)
    private val meta  get() = firestore.collection(COL_META).document(DOC_STATE)

    /* ---------------------- Pages ---------------------- */

    suspend fun isPageSaved(page: Int): Boolean =
        runCatching { pages.document(page.toString()).get().await().exists() }.getOrDefault(false)

    suspend fun savePage(page: Int, lastPage: Int, items: List<HadithDto>) {
        val pageDoc = pages.document(page.toString())
        val itemsCol = pageDoc.collection(SUB_ITEMS)

        // Batch the items + page meta in chunks of 450 to stay under the 500-op limit.
        val ids = items.map { it.id }
        items.chunked(450).forEach { chunk ->
            val batch = firestore.batch()
            chunk.forEach { dto -> batch.set(itemsCol.document(dto.id.toString()), dto.toMap()) }
            batch.commit().await()
        }
        pageDoc.set(
            mapOf(
                "page" to page,
                "lastPage" to lastPage,
                "count" to items.size,
                "savedAt" to System.currentTimeMillis(),
                "ids" to ids,
            )
        ).await()
    }

    /** Returns the ordered list of hadith ids for [page] (or empty if page not saved). */
    suspend fun pageIds(page: Int): List<Int> =
        runCatching {
            val snap = pages.document(page.toString()).get().await()
            @Suppress("UNCHECKED_CAST")
            (snap.get("ids") as? List<Long>)?.map { it.toInt() } ?: emptyList()
        }.getOrDefault(emptyList())

    suspend fun getHadith(page: Int, hadithId: Int): Map<String, Any?>? =
        runCatching {
            pages.document(page.toString())
                .collection(SUB_ITEMS)
                .document(hadithId.toString())
                .get().await().data
        }.getOrNull()

    /* ---------------------- Meta cursor ---------------------- */

    suspend fun loadMeta(): MetaState? = runCatching {
        val snap = meta.get().await()
        if (!snap.exists()) return@runCatching null
        MetaState(
            lastShownDate = snap.getString("lastShownDate") ?: "",
            currentPage = (snap.getLong("currentPage") ?: 1L).toInt(),
            currentIndex = (snap.getLong("currentIndex") ?: 0L).toInt(),
            currentHadithId = (snap.getLong("currentHadithId") ?: 0L).toInt(),
            lastFetchedPage = (snap.getLong("lastFetchedPage") ?: 0L).toInt(),
            totalPages = (snap.getLong("totalPages") ?: 0L).toInt(),
        )
    }.getOrNull()

    suspend fun saveMeta(state: MetaState) {
        meta.set(
            mapOf(
                "lastShownDate" to state.lastShownDate,
                "currentPage" to state.currentPage,
                "currentIndex" to state.currentIndex,
                "currentHadithId" to state.currentHadithId,
                "lastFetchedPage" to state.lastFetchedPage,
                "totalPages" to state.totalPages,
                "updatedAt" to System.currentTimeMillis(),
            )
        ).await()
    }

    data class MetaState(
        val lastShownDate: String,
        val currentPage: Int,
        val currentIndex: Int,
        val currentHadithId: Int,
        val lastFetchedPage: Int,
        val totalPages: Int,
    )

    private companion object {
        const val COL_PAGES = "hadith_pages"
        const val SUB_ITEMS = "items"
        const val COL_META  = "hadith_meta"
        const val DOC_STATE = "state"
    }
}

/** Map a HadithDto into a primitive map suitable for Firestore. */
private fun HadithDto.toMap(): Map<String, Any?> = mapOf(
    "id" to id,
    "hadithNumber" to hadithNumber,
    "englishNarrator" to englishNarrator,
    "hadithEnglish" to hadithEnglish,
    "hadithUrdu" to hadithUrdu,
    "urduNarrator" to urduNarrator,
    "hadithArabic" to hadithArabic,
    "headingArabic" to headingArabic,
    "headingUrdu" to headingUrdu,
    "headingEnglish" to headingEnglish,
    "chapterId" to chapterId,
    "bookSlug" to bookSlug,
    "volume" to volume,
    "status" to status,
    "bookName" to book?.bookName,
    "writerName" to book?.writerName,
    "chapterEnglish" to chapter?.chapterEnglish,
    "chapterUrdu" to chapter?.chapterUrdu,
    "chapterArabic" to chapter?.chapterArabic,
)

