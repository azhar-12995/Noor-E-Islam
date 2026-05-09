package com.azhar.noor_e_islam.presentation.qibla

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/** UI state for the Qibla compass. */
data class QiblaUiState(
    val hasPermission: Boolean = false,
    val sensorsAvailable: Boolean = true,
    val isLocating: Boolean = false,
    val location: Location? = null,
    val azimuth: Float = 0f,            // device heading from true north (degrees)
    val qiblaBearing: Float = 0f,       // bearing from user to Kaaba (degrees)
    val errorMessage: String? = null,
) {
    /** Rotation to apply to the needle so it points to Qibla, given the device azimuth. */
    val needleRotation: Float get() = (qiblaBearing - azimuth + 360f) % 360f

    /** True when device is roughly facing Qibla (±5°). */
    val isAligned: Boolean
        get() {
            val diff = ((needleRotation + 540f) % 360f) - 180f
            return kotlin.math.abs(diff) <= 5f
        }
}

@HiltViewModel
class QiblaViewModel @Inject constructor(
    private val sensorHandler: SensorHandler,
    private val locationService: LocationService,
) : ViewModel() {

    private val _state = MutableStateFlow(
        QiblaUiState(
            hasPermission = locationService.hasLocationPermission(),
            sensorsAvailable = sensorHandler.hasRequiredSensors,
        )
    )
    val state: StateFlow<QiblaUiState> = _state.asStateFlow()

    private var sensorJob: Job? = null
    private var locationJob: Job? = null

    /** Call when permission has been (re)checked or granted. */
    fun onPermissionResult(granted: Boolean) {
        _state.value = _state.value.copy(hasPermission = granted)
        if (granted) start()
    }

    fun start() {
        if (!_state.value.sensorsAvailable) return
        if (!_state.value.hasPermission) {
            // We can still show a magnetic-north compass; bearing will be 0 until we get a location.
            startSensors()
            return
        }
        startLocation()
        startSensors()
    }

    private fun startSensors() {
        sensorJob?.cancel()
        sensorJob = sensorHandler.azimuthFlow { _state.value.location }
            .onEach { az -> _state.value = _state.value.copy(azimuth = az) }
            .launchIn(viewModelScope)
    }

    private fun startLocation() {
        if (locationJob?.isActive == true) return
        _state.value = _state.value.copy(isLocating = true)
        locationJob = viewModelScope.launch {
            // 1. Try one-shot last/current location.
            val first = locationService.currentLocation()
            if (first != null) updateLocation(first)
            else _state.value = _state.value.copy(isLocating = true)

            // 2. Stream updates for live correction.
            locationService.locationUpdates()
                .onEach(::updateLocation)
                .launchIn(this)
        }
    }

    private fun updateLocation(loc: Location) {
        val bearing = QiblaCalculator.bearingToKaaba(loc.latitude, loc.longitude)
        _state.value = _state.value.copy(
            location = loc,
            qiblaBearing = bearing,
            isLocating = false,
            errorMessage = null,
        )
    }

    fun stop() {
        sensorJob?.cancel(); sensorJob = null
        locationJob?.cancel(); locationJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stop()
    }
}

