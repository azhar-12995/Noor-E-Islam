package com.azhar.noor_e_islam.data.repository

import com.azhar.noor_e_islam.core.util.HijriCalendarUtil
import com.azhar.noor_e_islam.domain.model.EventCategory
import com.azhar.noor_e_islam.domain.model.IslamicEvent
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Recurring Islamic events keyed by Hijri month/day. The actual Gregorian
 * dates are computed dynamically per Gregorian year via [HijriCalendarUtil],
 * so events are correct for ANY year (2026, 2027, 2030, 2050…).
 */
internal object HijriEventCatalog {

    private data class Spec(
        val hijriMonth: Int,   // 1..12
        val hijriDay: Int,     // 1..30
        val title: String,
        val category: EventCategory,
        val description: String = "",
    )

    private val SPECS = listOf(
        Spec(1, 1,  "1st Muharram",       EventCategory.MUHARRAM,
            "The first day of the Islamic New Year."),
        Spec(1, 10, "Ashura",             EventCategory.MUHARRAM,
            "A day of fasting and remembrance, commemorating the martyrdom of Imam Hussain (RA)."),
        Spec(2, 20, "Chehlum (Arbaeen)",  EventCategory.MUHARRAM,
            "Marks the 40th day after the martyrdom of Imam Hussain (RA) at Karbala."),
        Spec(3, 12, "Eid Milad un Nabi",  EventCategory.MILAD,
            "Commemorates the birth of Prophet Muhammad ﷺ."),
        Spec(4, 11, "Gyarvi Sharif",      EventCategory.GENERAL,
            "Commemorating Sheikh Abdul Qadir Jilani (RA)."),
        Spec(7, 13, "Youm e Ali",         EventCategory.REMEMBRANCE,
            "Birthday of Hazrat Ali (RA)."),
        Spec(7, 27, "Shab e Miraj",       EventCategory.HOLY_NIGHT,
            "The Night Journey of Prophet Muhammad ﷺ from Makkah to Jerusalem and his ascension to the heavens."),
        Spec(8, 15, "Shab e Barat",       EventCategory.HOLY_NIGHT,
            "The Night of Forgiveness, a blessed night to seek Allah's mercy and forgiveness."),
        Spec(9, 1,  "Ramadan",            EventCategory.RAMADAN,
            "The first day of the holy month of fasting."),
        Spec(9, 27, "Laylat Al Qadr",     EventCategory.HOLY_NIGHT,
            "The Night of Power — better than a thousand months."),
        Spec(10, 1, "Eid ul Fitr",        EventCategory.EID,
            "Festival marking the end of the holy month of Ramadan."),
        Spec(12, 9, "Hajj",               EventCategory.HAJJ,
            "The Day of Arafah, the most sacred day of Hajj pilgrimage."),
        Spec(12, 10,"Eid ul Adha",        EventCategory.EID,
            "Festival of Sacrifice commemorating Prophet Ibrahim's willingness to sacrifice his son."),
    )

    private val isoFmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val weekdayFmt = SimpleDateFormat("EEEE", Locale.getDefault())

    /**
     * Generate every event whose computed Gregorian date falls within [gregorianYear].
     * A single Hijri date can occur 0, 1, or 2 times in any Gregorian year.
     */
    fun forGregorianYear(gregorianYear: Int): List<IslamicEvent> {
        val hijriYears = HijriCalendarUtil.hijriYearsInGregorianYear(gregorianYear)
        if (hijriYears.isEmpty()) return emptyList()

        val out = mutableListOf<IslamicEvent>()
        for (hy in hijriYears) {
            for (s in SPECS) {
                val cal = HijriCalendarUtil.hijriToGregorian(hy, s.hijriMonth, s.hijriDay) ?: continue
                if (cal.get(java.util.Calendar.YEAR) != gregorianYear) continue
                val iso = isoFmt.format(cal.time)
                out += IslamicEvent(
                    id = IslamicEvent.makeId(iso, s.title),
                    title = s.title,
                    hijriDate = "${ordinal(s.hijriDay)} ${hijriMonthName(s.hijriMonth)} ${hy}h",
                    gregorianDateIso = iso,
                    year = cal.get(java.util.Calendar.YEAR),
                    month = cal.get(java.util.Calendar.MONTH) + 1,
                    day = cal.get(java.util.Calendar.DAY_OF_MONTH),
                    weekday = weekdayFmt.format(cal.time),
                    category = s.category,
                    description = s.description,
                )
            }
        }
        return out.sortedBy { it.gregorianDateIso }
    }

    private fun ordinal(d: Int): String = when {
        d in 11..13 -> "${d}th"
        d % 10 == 1 -> "${d}st"
        d % 10 == 2 -> "${d}nd"
        d % 10 == 3 -> "${d}rd"
        else -> "${d}th"
    }

    private fun hijriMonthName(m: Int): String = listOf(
        "", "Muharram", "Safar", "Rabi al-Awwal", "Rabi al-Thani",
        "Jumada al-Ula", "Jumada al-Akhirah", "Rajab", "Shaban",
        "Ramadan", "Shawwal", "Dhul-Qidah", "Dhul-Hijjah"
    ).getOrElse(m) { "" }
}

