package com.azhar.noor_e_islam.presentation.qibla

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Pure offline Qibla bearing calculator.
 *
 * Uses the great-circle initial bearing formula from the user's location to the Kaaba.
 * The result is a true-north bearing in degrees in the range [0, 360).
 */
object QiblaCalculator {

    /** Geographic coordinates of the Kaaba in Makkah. */
    const val KAABA_LATITUDE  = 21.4225
    const val KAABA_LONGITUDE = 39.8262

    /**
     * Returns the Qibla bearing (relative to true north) in degrees [0, 360).
     *
     * @param userLat user latitude in degrees
     * @param userLng user longitude in degrees
     */
    fun bearingToKaaba(userLat: Double, userLng: Double): Float {
        val phi1 = Math.toRadians(userLat)
        val phi2 = Math.toRadians(KAABA_LATITUDE)
        val deltaLambda = Math.toRadians(KAABA_LONGITUDE - userLng)

        val y = sin(deltaLambda) * cos(phi2)
        val x = cos(phi1) * sin(phi2) - sin(phi1) * cos(phi2) * cos(deltaLambda)
        val theta = atan2(y, x)

        // Normalize to [0, 360)
        val deg = (Math.toDegrees(theta) + 360.0) % 360.0
        return deg.toFloat()
    }
}

