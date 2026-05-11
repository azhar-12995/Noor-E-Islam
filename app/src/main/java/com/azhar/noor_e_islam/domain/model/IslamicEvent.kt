package com.azhar.noor_e_islam.domain.model

/**
 * Islamic event mapped to a specific Gregorian date.
 *
 * [id] is a deterministic key built from the gregorian date + slug of the title
 * so that re-seeding never produces duplicates in Firestore.
 */
data class IslamicEvent(
    val id: String,
    val title: String,
    val hijriDate: String,
    val gregorianDateIso: String, // yyyy-MM-dd
    val year: Int,
    val month: Int,             // 1..12
    val day: Int,               // 1..31
    val weekday: String = "",
    val category: EventCategory = EventCategory.GENERAL,
    val description: String = "",
) {
    companion object {
        /** Stable id used as Firestore doc id — prevents duplicates on re-seed. */
        fun makeId(gregorianDateIso: String, title: String): String {
            val slug = title.lowercase()
                .replace(Regex("[^a-z0-9]+"), "-")
                .trim('-')
            return "${gregorianDateIso}_$slug"
        }
    }
}

enum class EventCategory {
    EID,       // Eid ul Fitr, Eid ul Adha
    HOLY_NIGHT, // Shab e Miraj, Shab e Barat, Laylat Al Qadr
    RAMADAN,
    HAJJ,
    MUHARRAM,   // 1st Muharram, Ashura, Chehlum
    MILAD,      // Eid Milad un Nabi
    REMEMBRANCE,// Youm e Ali
    GENERAL;
}

