package com.azhar.noor_e_islam.presentation.qibla

import android.content.Context
import android.hardware.GeomagneticField
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

/**
 * Wraps the device [SensorManager] and exposes a smoothed *true-north* azimuth (degrees, 0..360).
 *
 * Pipeline:
 *  1. Read accelerometer + magnetometer values.
 *  2. Compute the rotation matrix and orientation array.
 *  3. Apply a low-pass filter for visual smoothness.
 *  4. Convert magnetic-north azimuth to **true north** using [GeomagneticField] declination
 *     (requires the user's last known [Location]).
 */
@Singleton
class SensorHandler @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val sensorManager: SensorManager? =
        context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager

    val hasRequiredSensors: Boolean
        get() = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null &&
                sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null

    /**
     * Cold flow that emits the smoothed *true-north* azimuth (degrees, 0..360).
     * Pass the most recent [Location] for geomagnetic declination correction.
     * If [locationProvider] returns null, the magnetic-north azimuth is emitted.
     */
    fun azimuthFlow(locationProvider: () -> Location?): Flow<Float> = callbackFlow {
        val sm = sensorManager
        val accel = sm?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnet = sm?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        if (sm == null || accel == null || magnet == null) {
            close(); return@callbackFlow
        }

        val gravity = FloatArray(3)
        val geomagnetic = FloatArray(3)
        val rotation = FloatArray(9)
        val orientation = FloatArray(3)
        var lastEmitted = Float.NaN

        // Low-pass smoothing factor (0..1). Higher = smoother but laggier.
        val alpha = 0.15f

        val listener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> lowPass(event.values, gravity, alpha)
                    Sensor.TYPE_MAGNETIC_FIELD -> lowPass(event.values, geomagnetic, alpha)
                }
                if (!SensorManager.getRotationMatrix(rotation, null, gravity, geomagnetic)) return
                SensorManager.getOrientation(rotation, orientation)

                // Magnetic-north azimuth in degrees, normalized to [0, 360)
                var azimuth = ((Math.toDegrees(orientation[0].toDouble()).toFloat() + 360f) % 360f)

                // Apply geomagnetic declination -> convert to true-north
                locationProvider()?.let { loc ->
                    val gmf = GeomagneticField(
                        loc.latitude.toFloat(),
                        loc.longitude.toFloat(),
                        loc.altitude.toFloat(),
                        System.currentTimeMillis()
                    )
                    azimuth = (azimuth + gmf.declination + 360f) % 360f
                }

                // De-duplicate sub-degree updates to limit recompositions
                if (lastEmitted.isNaN() || abs(angularDelta(lastEmitted, azimuth)) >= 0.5f) {
                    lastEmitted = azimuth
                    trySend(azimuth)
                }
            }
        }

        sm.registerListener(listener, accel, SensorManager.SENSOR_DELAY_GAME)
        sm.registerListener(listener, magnet, SensorManager.SENSOR_DELAY_GAME)

        awaitClose { sm.unregisterListener(listener) }
    }

    private fun lowPass(input: FloatArray, output: FloatArray, alpha: Float) {
        for (i in input.indices) {
            output[i] = output[i] + alpha * (input[i] - output[i])
        }
    }

    private fun angularDelta(a: Float, b: Float): Float {
        val d = ((b - a + 540f) % 360f) - 180f
        return d
    }
}

