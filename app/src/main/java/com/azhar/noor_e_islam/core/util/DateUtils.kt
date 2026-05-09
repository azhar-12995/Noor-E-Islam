package com.azhar.noor_e_islam.core.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/** Lightweight date helpers — production should use ICU HijriCalendar/IslamicCalendar. */
object DateUtils {

    fun gregorianToday(): String =
        SimpleDateFormat("EEEE, d MMM yyyy", Locale.getDefault()).format(Date())

    /** Naive Hijri estimate: Gregorian date offset by ~ -622 years and Muharram start.
     *  Replace with android.icu.util.IslamicCalendar for accurate dates. */
    fun hijriToday(): String {
        return try {
            val islamic = android.icu.util.IslamicCalendar()
            val months = arrayOf(
                "Muharram", "Safar", "Rabi' al-Awwal", "Rabi' al-Thani",
                "Jumada al-Awwal", "Jumada al-Thani", "Rajab", "Sha'ban",
                "Ramadan", "Shawwal", "Dhu al-Qa'dah", "Dhu al-Hijjah"
            )
            val day   = islamic.get(Calendar.DAY_OF_MONTH)
            val month = months.getOrElse(islamic.get(Calendar.MONTH)) { "" }
            val year  = islamic.get(Calendar.YEAR)
            "$day $month $year AH"
        } catch (_: Throwable) {
            ""
        }
    }
}

