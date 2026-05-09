package com.azhar.noor_e_islam.core.util

object Constants {
    const val DATABASE_NAME = "noor_e_islam.db"
    const val PREFS_NAME    = "noor_user_prefs"

    // Public APIs we may use
    const val QURAN_API_BASE  = "https://api.alquran.cloud/v1/"
    const val ALADHAN_API_BASE = "https://api.aladhan.com/v1/"
    const val HADITH_API_BASE  = "https://hadithapi.com/api/"
    // hadithapi.com personal access key
    const val HADITH_API_KEY   = "\$2y\$10\$GtXm6L1otq5IACl1Z8rhjuo4PYUCYnWywLnv14yaT0teJqSOVDO"

    // Firestore collections
    const val COL_USERS       = "users"
    const val COL_BOOKMARKS   = "bookmarks"
    const val COL_NOTES       = "notes"
    const val COL_HABITS      = "habits"
    const val COL_PROGRESS    = "reading_progress"
    const val COL_INCIDENTS   = "incidents"
    const val COL_STORIES     = "stories"
    const val COL_DUAS        = "duas"

    // Deep link
    const val DEEP_LINK_SCHEME = "noor"
}

