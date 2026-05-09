package com.azhar.noor_e_islam.presentation.prayertimes

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azhar.noor_e_islam.presentation.qibla.LocationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import javax.inject.Inject

data class PrayerTimesUiState(
    val hasPermission: Boolean = false,
    val isLoading: Boolean = true,
    val location: Location? = null,
    val timeZoneId: String = TimeZone.getDefault().id,
    val times: PrayerTimesCalculator.PrayerTimes? = null,
    val nextPrayer: PrayerTimesCalculator.Prayer? = null,
    val currentPrayer: PrayerTimesCalculator.Prayer? = null,
    val nowHours: Double = 0.0,
    val error: String? = null,
)

@HiltViewModel
class PrayerTimesViewModel @Inject constructor(
    private val locationService: LocationService,
) : ViewModel() {

    private val _state = MutableStateFlow(
        PrayerTimesUiState(hasPermission = locationService.hasLocationPermission())
    )
    val state: StateFlow<PrayerTimesUiState> = _state.asStateFlow()

    private var tickerJob: Job? = null

    fun onPermissionResult(granted: Boolean) {
        _state.value = _state.value.copy(hasPermission = granted)
        if (granted) refresh()
    }

    /** Recompute using current device location. */
    fun refresh() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val loc = locationService.currentLocation()
            if (loc == null) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Couldn't determine location"
                )
                return@launch
            }
            recompute(loc)
            startTicker()
        }
    }

    private fun recompute(loc: Location) {
        val tz = TimeZone.getDefault()
        val now = Calendar.getInstance(tz)
        val times = PrayerTimesCalculator.compute(
            date = Date(),
            latitude = loc.latitude,
            longitude = loc.longitude,
            timeZone = tz,
        )
        val nowHours = now.get(Calendar.HOUR_OF_DAY) +
            now.get(Calendar.MINUTE) / 60.0 +
            now.get(Calendar.SECOND) / 3600.0
        val (next, prev) = PrayerTimesCalculator.nextPrayer(nowHours, times)
        _state.value = _state.value.copy(
            isLoading = false,
            location = loc,
            timeZoneId = tz.id,
            times = times,
            nextPrayer = next,
            currentPrayer = prev,
            nowHours = nowHours,
            error = null,
        )
    }

    private fun startTicker() {
        tickerJob?.cancel()
        tickerJob = viewModelScope.launch {
            while (true) {
                delay(30_000L) // refresh "next prayer" every 30s
                val loc = _state.value.location ?: return@launch
                recompute(loc)
            }
        }
    }

    fun stop() {
        tickerJob?.cancel(); tickerJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stop()
    }
}

