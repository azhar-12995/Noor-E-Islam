package com.azhar.noor_e_islam.presentation.prayertimes

import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.TimeZone
import kotlin.math.*

/**
 * Fully offline Islamic prayer-time calculator.
 *
 * Adapted from the public-domain PrayTimes.org astronomical algorithm
 * (https://praytimes.org/calculation). All computations are pure math
 * based on the date, latitude, longitude and timezone — no network
 * required.
 *
 * Returned times are minutes since midnight in the user's local timezone.
 */
object PrayerTimesCalculator {

    enum class Method(
        val fajrAngle: Double,
        val ishaAngle: Double,
        val ishaInterval: Int = 0, // minutes after Maghrib (overrides angle if > 0)
        val maghribAngle: Double = 0.0,
    ) {
        MWL(18.0, 17.0),                    // Muslim World League
        ISNA(15.0, 15.0),                   // Islamic Society of North America
        EGYPT(19.5, 17.5),                  // Egyptian General Authority
        MAKKAH(18.5, 0.0, ishaInterval = 90), // Umm al-Qura, Makkah
        KARACHI(18.0, 18.0),                // University of Islamic Sciences, Karachi
        TEHRAN(17.7, 14.0, maghribAngle = 4.5),
        JAFARI(16.0, 14.0, maghribAngle = 4.0),
    }

    enum class AsrJuristic(val factor: Int) {
        STANDARD(1), // Shafi'i / Maliki / Hanbali
        HANAFI(2),
    }

    data class PrayerTimes(
        val fajr: Double,     // hours since local midnight (e.g. 5.25 = 05:15)
        val sunrise: Double,
        val dhuhr: Double,
        val asr: Double,
        val maghrib: Double,
        val isha: Double,
    ) {
        operator fun get(prayer: Prayer): Double = when (prayer) {
            Prayer.FAJR -> fajr
            Prayer.SUNRISE -> sunrise
            Prayer.DHUHR -> dhuhr
            Prayer.ASR -> asr
            Prayer.MAGHRIB -> maghrib
            Prayer.ISHA -> isha
        }
    }

    enum class Prayer(val displayName: String) {
        FAJR("Fajr"),
        SUNRISE("Sunrise"),
        DHUHR("Dhuhr"),
        ASR("Asr"),
        MAGHRIB("Maghrib"),
        ISHA("Isha"),
    }

    /**
     * Compute prayer times for the given [date], [latitude], [longitude] and [timeZone].
     */
    fun compute(
        date: Date,
        latitude: Double,
        longitude: Double,
        timeZone: TimeZone = TimeZone.getDefault(),
        method: Method = Method.MWL,
        asr: AsrJuristic = AsrJuristic.STANDARD,
    ): PrayerTimes {
        val cal = GregorianCalendar(timeZone).apply { time = date }
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)

        // Timezone offset from UTC in hours, including DST for the given date.
        val tzOffsetHours = timeZone.getOffset(date.time) / 3_600_000.0
        // Julian Date for noon, adjusted to local solar time.
        val jd = julianDate(year, month, day) - longitude / (15.0 * 24.0)

        // Initial guesses (in hours)
        var fajr = 5.0 / 24.0
        var sunrise = 6.0 / 24.0
        var dhuhr = 12.0 / 24.0
        var asrT = 13.0 / 24.0
        var sunset = 18.0 / 24.0
        var maghrib = 18.0 / 24.0
        var isha = 18.0 / 24.0

        // Iterate twice for accuracy.
        repeat(2) {
            fajr     = sunAngleTime(method.fajrAngle, jd + fajr,    latitude, ccw = true)
            sunrise  = sunAngleTime(0.833,            jd + sunrise, latitude, ccw = true)
            dhuhr    = computeMidDay(jd + dhuhr)
            asrT     = computeAsr(asr.factor, jd + asrT, latitude)
            sunset   = sunAngleTime(0.833,            jd + sunset,  latitude, ccw = false)
            maghrib  = if (method.maghribAngle > 0.0) sunAngleTime(method.maghribAngle, jd + maghrib, latitude, ccw = false) else sunset
            isha     = if (method.ishaInterval > 0) maghrib + method.ishaInterval / 60.0 / 24.0
                       else sunAngleTime(method.ishaAngle, jd + isha, latitude, ccw = false)
        }

        // Convert from days to hours, adjust for timezone.
        fun adjust(t: Double) = ((t * 24.0) + tzOffsetHours - longitude / 15.0).mod(24.0)

        return PrayerTimes(
            fajr     = adjust(fajr),
            sunrise  = adjust(sunrise),
            dhuhr    = adjust(dhuhr) + 1.0 / 60.0,   // small correction
            asr      = adjust(asrT),
            maghrib  = adjust(maghrib),
            isha     = adjust(isha),
        )
    }

    /** Format an hours-since-midnight value to a 12h clock string e.g. "05:14 AM". */
    fun formatTime(hours: Double, use24h: Boolean = false): String {
        val total = (hours * 60.0 + 0.5).toInt() // round to nearest minute
        val h24 = ((total / 60) % 24 + 24) % 24
        val m = ((total % 60) + 60) % 60
        return if (use24h) {
            "%02d:%02d".format(h24, m)
        } else {
            val ampm = if (h24 >= 12) "PM" else "AM"
            val h12 = ((h24 + 11) % 12) + 1
            "%02d:%02d %s".format(h12, m, ampm)
        }
    }

    /** Find the upcoming prayer (and the previous one) for [nowHours]. */
    fun nextPrayer(now: Double, times: PrayerTimes): Pair<Prayer, Prayer> {
        // Order matters; Sunrise is informational but we still treat it as a checkpoint.
        val ordered = listOf(
            Prayer.FAJR to times.fajr,
            Prayer.SUNRISE to times.sunrise,
            Prayer.DHUHR to times.dhuhr,
            Prayer.ASR to times.asr,
            Prayer.MAGHRIB to times.maghrib,
            Prayer.ISHA to times.isha,
        )
        for (i in ordered.indices) {
            if (now < ordered[i].second) {
                val prev = if (i == 0) Prayer.ISHA else ordered[i - 1].first
                return ordered[i].first to prev
            }
        }
        // After Isha → next prayer is tomorrow's Fajr; current period is Isha.
        return Prayer.FAJR to Prayer.ISHA
    }

    /* ----------------------- Internal helpers ----------------------- */

    private fun julianDate(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) { y -= 1; m += 12 }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    /** Sun declination & equation of time (returns degrees & hours). */
    private fun sunPosition(jd: Double): Pair<Double, Double> {
        val d = jd - 2451545.0
        val g = fixAngle(357.529 + 0.98560028 * d)
        val q = fixAngle(280.459 + 0.98564736 * d)
        val l = fixAngle(q + 1.915 * dSin(g) + 0.020 * dSin(2 * g))
        val e = 23.439 - 0.00000036 * d
        val ra = dArcTan2(dCos(e) * dSin(l), dCos(l)) / 15.0
        val decl = dArcSin(dSin(e) * dSin(l))
        val eqt = q / 15.0 - fixHour(ra)
        return decl to eqt
    }

    private fun computeMidDay(jd: Double): Double {
        val (_, eqt) = sunPosition(jd)
        return fixHour(12.0 - eqt) / 24.0
    }

    /** Compute time at which the sun is at given [angle] below the horizon. */
    private fun sunAngleTime(angle: Double, jd: Double, lat: Double, ccw: Boolean): Double {
        val (decl, _) = sunPosition(jd)
        val noon = computeMidDay(jd) * 24.0
        val cosH = (-dSin(angle) - dSin(lat) * dSin(decl)) / (dCos(lat) * dCos(decl))
        if (cosH > 1.0 || cosH < -1.0) return noon / 24.0 // sun never reaches this angle
        val t = dArcCos(cosH) / 15.0
        return (noon + if (ccw) -t else t) / 24.0
    }

    private fun computeAsr(factor: Int, jd: Double, lat: Double): Double {
        val (decl, _) = sunPosition(jd)
        val angle = -dArcCot(factor + dTan(abs(lat - decl)))
        return sunAngleTime(angle, jd, lat, ccw = false)
    }

    /* Trig helpers in degrees */
    private fun dSin(d: Double) = sin(Math.toRadians(d))
    private fun dCos(d: Double) = cos(Math.toRadians(d))
    private fun dTan(d: Double) = tan(Math.toRadians(d))
    private fun dArcSin(x: Double) = Math.toDegrees(asin(x))
    private fun dArcCos(x: Double) = Math.toDegrees(acos(x))
    private fun dArcTan2(y: Double, x: Double) = Math.toDegrees(atan2(y, x))
    private fun dArcCot(x: Double) = Math.toDegrees(atan2(1.0, x))

    private fun fixAngle(a: Double) = ((a % 360.0) + 360.0) % 360.0
    private fun fixHour(a: Double)  = ((a % 24.0) + 24.0) % 24.0
}

