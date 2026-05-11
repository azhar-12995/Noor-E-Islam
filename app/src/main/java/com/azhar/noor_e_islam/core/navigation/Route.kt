package com.azhar.noor_e_islam.core.navigation

/**
 * Sealed declaration of all navigation destinations.
 * Use [route] when calling NavController.navigate.
 */
sealed class Route(val route: String) {
    // Bootstrap
    data object Splash      : Route("splash")
    data object Onboarding  : Route("onboarding")

    // Auth
    data object Login       : Route("auth/login")
    data object Register    : Route("auth/register")
    data object Forgot      : Route("auth/forgot")

    // Bottom-nav root
    data object Home        : Route("home")
    data object QuranList   : Route("quran/list")
    data object Learn       : Route("learn")
    data object Calendar    : Route("calendar")
    data object Profile     : Route("profile")

    // Quran sub-flows
    data object QuranReader : Route("quran/reader/{surahId}?ayah={ayah}") {
        fun create(surahId: Int, ayah: Int? = null): String =
            if (ayah != null && ayah > 0) "quran/reader/$surahId?ayah=$ayah"
            else "quran/reader/$surahId?ayah=0"
    }
    data object QuranSettings : Route("quran/settings")
    data object QuranShare    : Route("quran/share/{surahId}/{ayah}") {
        fun create(surahId: Int, ayah: Int) = "quran/share/$surahId/$ayah"
    }

    // Other features
    data object Incidents   : Route("incidents")
    data object IncidentDetail : Route("incidents/{id}") { fun create(id: String) = "incidents/$id" }
    data object Dua         : Route("dua")
    data object DuaDetail   : Route("dua/{id}") { fun create(id: String) = "dua/$id" }
    data object Stories     : Route("stories")
    data object StoryDetail : Route("stories/{id}") { fun create(id: String) = "stories/$id" }
    data object Habits      : Route("habits")
    data object Bookmarks   : Route("bookmarks")
    data object Notes       : Route("notes")
    data object NoteEditor  : Route("notes/editor?id={id}") {
        fun create(id: String? = null) = "notes/editor?id=${id.orEmpty()}"
    }
    data object Progress    : Route("progress")
    data object Settings    : Route("settings")
    data object About       : Route("about")
    data object Hadith      : Route("hadith")
    data object Qibla       : Route("qibla")
    data object PrayerTimes : Route("prayer-times")

    // User-area additions
    data object Notifications : Route("notifications")
    data object EditProfile   : Route("profile/edit")
    data object Feedback      : Route("feedback?highlight={highlight}") {
        fun create(highlight: String? = null) =
            if (highlight.isNullOrBlank()) "feedback?highlight="
            else "feedback?highlight=$highlight"
    }

    // Admin
    data object AdminDashboard : Route("admin/dashboard")

    companion object {
        val bottomNav = listOf(Home, QuranList, Learn, Calendar, Profile)
    }
}

