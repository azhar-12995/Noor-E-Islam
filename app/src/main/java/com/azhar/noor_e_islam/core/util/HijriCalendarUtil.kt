package com.azhar.noor_e_islam.core.util

import android.icu.util.IslamicCalendar
import android.icu.util.ULocale
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.TimeZone

/**
 * Hijri ↔ Gregorian conversion using ICU's [IslamicCalendar] (Umm al-Qura
 * civil arithmetic), available on API 24+ which matches our minSdk.
 *
 * All math is fully offline.
 */
object HijriCalendarUtil {

    private fun newIslamic(): IslamicCalendar {
        // Use ULocale-based ctor with a TimeZone set explicitly afterwards
        // to avoid ambiguity with java.util.TimeZone.
        val cal = IslamicCalendar(ULocale.getDefault())
        cal.timeZone = android.icu.util.TimeZone.getDefault()
        return cal
    }

    /** Convert a Hijri (year/month/day, month is 1..12) to a Gregorian [Calendar]. */
    fun hijriToGregorian(hijriYear: Int, hijriMonth1Based: Int, hijriDay: Int): Calendar? {
        val islamic = newIslamic()
        islamic.clear()
        islamic.set(IslamicCalendar.ERA, 1)
        islamic.set(IslamicCalendar.YEAR, hijriYear)
        islamic.set(IslamicCalendar.MONTH, hijriMonth1Based - 1) // ICU is 0-based
        islamic.set(IslamicCalendar.DAY_OF_MONTH, hijriDay)
        // ICU Calendar uses time methods, not a Kotlin `timeInMillis` property.
        val millis = islamic.time.time
        return GregorianCalendar(TimeZone.getDefault()).apply { timeInMillis = millis }
    }

    /** Get the Hijri year(s) that overlap a given Gregorian year. */
    fun hijriYearsInGregorianYear(gregorianYear: Int): List<Int> {
        val years = mutableSetOf<Int>()
        listOf(0 to 1, 5 to 1, 11 to 31).forEach { (monthIdx, day) ->
            val cal = GregorianCalendar(gregorianYear, monthIdx, day)
            val islamic = newIslamic()
            islamic.time = java.util.Date(cal.timeInMillis)
            years.add(islamic.get(IslamicCalendar.YEAR))
        }
        return years.sorted()
    }
}
