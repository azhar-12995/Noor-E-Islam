package com.azhar.noor_e_islam.presentation.prayertimes

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.azhar.noor_e_islam.ui.theme.Emerald900
import com.azhar.noor_e_islam.ui.theme.Gold500
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlin.math.abs
import kotlin.math.floor

private val prayerIcons: Map<PrayerTimesCalculator.Prayer, ImageVector> = mapOf(
    PrayerTimesCalculator.Prayer.FAJR to Icons.Filled.Brightness4,
    PrayerTimesCalculator.Prayer.SUNRISE to Icons.Filled.WbTwilight,
    PrayerTimesCalculator.Prayer.DHUHR to Icons.Filled.WbSunny,
    PrayerTimesCalculator.Prayer.ASR to Icons.Filled.Brightness6,
    PrayerTimesCalculator.Prayer.MAGHRIB to Icons.Filled.NightsStay,
    PrayerTimesCalculator.Prayer.ISHA to Icons.Filled.Bedtime,
)

/* --------------------------- Full screen --------------------------- */

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun PrayerTimesScreen(
    onBack: () -> Unit,
    viewModel: PrayerTimesViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val permission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(permission.status.isGranted) {
        viewModel.onPermissionResult(permission.status.isGranted)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Prayer Times", color = Color.White, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Filled.Refresh, "Refresh", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Emerald900,
                    titleContentColor = Color.White,
                ),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
        ) {
            when {
                !permission.status.isGranted -> PermissionPrompt { permission.launchPermissionRequest() }
                state.isLoading && state.times == null -> LoadingPanel()
                state.times == null -> ErrorPanel(state.error ?: "Unable to compute prayer times")
                else -> {
                    NextPrayerHero(state)
                    Spacer(Modifier.height(20.dp))
                    Text(
                        "Today's Schedule",
                        color = Emerald900,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    )
                    Spacer(Modifier.height(8.dp))
                    PrayerList(state)
                    Spacer(Modifier.height(16.dp))
                    LocationFooter(state)
                }
            }
        }
    }
}

/* --------------------------- Compact home card --------------------------- */

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PrayerTimesHomeCard(
    onOpenFull: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PrayerTimesViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val permission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(permission.status.isGranted) {
        viewModel.onPermissionResult(permission.status.isGranted)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        color = Gold500.copy(alpha = 0.14f),
        onClick = onOpenFull,
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val nextIcon = state.nextPrayer?.let { prayerIcons[it] } ?: Icons.Filled.WbSunny
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Emerald900),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(nextIcon, null, tint = Gold500, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        "Prayer Times",
                        color = Emerald900,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                    )
                    Text(
                        when {
                            !permission.status.isGranted -> "Tap to allow location"
                            state.isLoading && state.times == null -> "Calculating…"
                            state.times == null -> "Unavailable"
                            state.nextPrayer != null -> {
                                val t = state.times!![state.nextPrayer!!]
                                val name = state.nextPrayer!!.displayName
                                val countdown = countdownText(state.nowHours, t)
                                "Next: $name in $countdown"
                            }
                            else -> ""
                        },
                        color = Emerald900.copy(alpha = 0.75f),
                        fontSize = 12.sp,
                    )
                }
                state.nextPrayer?.let { p ->
                    state.times?.let { t ->
                        Text(
                            PrayerTimesCalculator.formatTime(t[p]),
                            color = Emerald900,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                        )
                    }
                }
            }
            state.times?.let { t ->
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    listOf(
                        PrayerTimesCalculator.Prayer.FAJR,
                        PrayerTimesCalculator.Prayer.DHUHR,
                        PrayerTimesCalculator.Prayer.ASR,
                        PrayerTimesCalculator.Prayer.MAGHRIB,
                        PrayerTimesCalculator.Prayer.ISHA,
                    ).forEach { p ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                p.displayName,
                                color = Emerald900.copy(alpha = 0.65f),
                                fontSize = 11.sp,
                            )
                            Text(
                                PrayerTimesCalculator.formatTime(t[p]).removeSuffix(" AM").removeSuffix(" PM"),
                                color = Emerald900,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }
        }
    }
}

/* --------------------------- UI pieces --------------------------- */

@Composable
private fun NextPrayerHero(state: PrayerTimesUiState) {
    val next = state.nextPrayer ?: return
    val times = state.times ?: return
    val target = times[next]
    Surface(
        color = Emerald900,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Next Prayer", color = Gold500, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text(
                next.displayName,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                PrayerTimesCalculator.formatTime(target),
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 18.sp,
            )
            Spacer(Modifier.height(10.dp))
            Text(
                "in ${countdownText(state.nowHours, target)}",
                color = Gold500,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun PrayerList(state: PrayerTimesUiState) {
    val times = state.times ?: return
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PrayerTimesCalculator.Prayer.values().forEach { p ->
            val isCurrent = state.currentPrayer == p
            val isNext = state.nextPrayer == p
            PrayerRow(
                name = p.displayName,
                time = PrayerTimesCalculator.formatTime(times[p]),
                icon = prayerIcons[p] ?: Icons.Filled.WbSunny,
                highlight = isCurrent || isNext,
                tag = when {
                    isNext -> "Next"
                    isCurrent -> "Now"
                    else -> null
                },
            )
        }
    }
}

@Composable
private fun PrayerRow(
    name: String,
    time: String,
    icon: ImageVector,
    highlight: Boolean,
    tag: String? = null,
) {
    Surface(
        color = if (highlight) Emerald900.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(14.dp),
        tonalElevation = if (highlight) 2.dp else 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (highlight) Emerald900 else Gold500.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    icon,
                    null,
                    tint = if (highlight) Gold500 else Emerald900,
                    modifier = Modifier.size(22.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                name,
                color = Emerald900,
                fontWeight = if (highlight) FontWeight.Bold else FontWeight.Medium,
                fontSize = 15.sp,
                modifier = Modifier.weight(1f),
            )
            tag?.let {
                Surface(
                    color = Gold500.copy(alpha = 0.22f),
                    shape = RoundedCornerShape(20.dp),
                ) {
                    Text(
                        it,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                        color = Gold500,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Spacer(Modifier.width(8.dp))
            }
            Text(time, color = Emerald900, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
    }
}

@Composable
private fun PermissionPrompt(onRequest: () -> Unit) {
    Spacer(Modifier.height(40.dp))
    Text(
        "Location needed",
        style = MaterialTheme.typography.titleLarge,
        color = Emerald900,
        fontWeight = FontWeight.Bold,
    )
    Spacer(Modifier.height(6.dp))
    Text(
        "Prayer times are calculated based on your latitude, longitude and timezone.",
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Spacer(Modifier.height(16.dp))
    Button(
        onClick = onRequest,
        colors = ButtonDefaults.buttonColors(containerColor = Emerald900, contentColor = Color.White),
    ) { Text("Grant location permission") }
}

@Composable
private fun LoadingPanel() {
    Spacer(Modifier.height(40.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        CircularProgressIndicator(color = Emerald900, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
        Spacer(Modifier.width(12.dp))
        Text("Calculating prayer times…", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ErrorPanel(message: String) {
    Spacer(Modifier.height(40.dp))
    Text(message, color = MaterialTheme.colorScheme.error)
}

@Composable
private fun LocationFooter(state: PrayerTimesUiState) {
    val loc = state.location ?: return
    Surface(color = Emerald900.copy(alpha = 0.05f), shape = RoundedCornerShape(12.dp)) {
        Text(
            "Region: ${state.timeZoneId} • %.4f, %.4f".format(loc.latitude, loc.longitude),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            color = Emerald900,
            fontSize = 12.sp,
        )
    }
}

/* --------------------------- Helpers --------------------------- */

private fun countdownText(now: Double, target: Double): String {
    var diff = target - now
    if (diff < 0) diff += 24.0
    val hours = floor(diff).toInt()
    val mins = ((diff - hours) * 60.0).toInt().coerceAtLeast(0)
    return when {
        hours > 0 -> "${hours}h ${mins}m"
        else -> "${abs(mins)}m"
    }
}

