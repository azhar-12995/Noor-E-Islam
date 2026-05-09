package com.azhar.noor_e_islam.presentation.qibla

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.azhar.noor_e_islam.ui.theme.Emerald900
import com.azhar.noor_e_islam.ui.theme.Gold500
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Full-screen Qibla compass screen.
 *
 * - Requests ACCESS_FINE_LOCATION at runtime.
 * - Observes [QiblaViewModel] state via StateFlow.
 * - Smooth animated needle + haptic when aligned (±5°).
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun QiblaScreen(
    onBack: () -> Unit,
    viewModel: QiblaViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val permission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    // Sync VM with the current permission status & start/stop on enter/leave.
    LaunchedEffect(permission.status.isGranted) {
        viewModel.onPermissionResult(permission.status.isGranted)
    }
    DisposableEffect(Unit) {
        viewModel.start()
        onDispose { viewModel.stop() }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Qibla Finder",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
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
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when {
                !state.sensorsAvailable -> ErrorPanel(
                    title = "Compass not supported",
                    message = "Your device does not have the required magnetometer/accelerometer sensors.",
                )

                !permission.status.isGranted -> PermissionPanel(
                    onRequest = { permission.launchPermissionRequest() }
                )

                state.isLocating && state.location == null -> LoadingPanel()

                else -> {
                    CompassView(
                        azimuth = state.azimuth,
                        needleRotation = state.needleRotation,
                        isAligned = state.isAligned,
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .size(300.dp),
                    )

                    Spacer(Modifier.height(24.dp))

                    Text(
                        text = "${state.needleRotation.toInt()}°",
                        style = MaterialTheme.typography.displaySmall,
                        color = Emerald900,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = if (state.isAligned) "Facing Qibla" else "Rotate to align",
                        color = if (state.isAligned) Gold500 else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold,
                    )

                    Spacer(Modifier.height(20.dp))
                    InfoRow(
                        bearing = state.qiblaBearing,
                        location = state.location,
                    )
                    Spacer(Modifier.height(16.dp))
                    CalibrationHint()
                }
            }
        }
    }

    // Vibrate briefly when alignment toggles to true
    val context = LocalContext.current
    val aligned = state.isAligned
    LaunchedEffect(aligned) { if (aligned) vibrateOnce(context) }
}

/* ----------------------------- Compass canvas ----------------------------- */

@Composable
private fun CompassView(
    azimuth: Float,
    needleRotation: Float,
    isAligned: Boolean,
    modifier: Modifier = Modifier,
) {
    // Smooth animated rotations.
    val animDial by animateFloatAsState(
        targetValue = -azimuth, // dial rotates opposite to device so N/E/S/W stay fixed
        animationSpec = tween(durationMillis = 250),
        label = "dial",
    )
    val animNeedle by animateFloatAsState(
        targetValue = needleRotation,
        animationSpec = tween(durationMillis = 250),
        label = "needle",
    )

    val ivory = Color(0xFFF5EFE0)
    val emerald = Emerald900
    val gold = Gold500

    val measurer = rememberTextMeasurer()
    val labelStyle = TextStyle(
        color = emerald,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
    )

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(ivory),
        contentAlignment = Alignment.Center,
    ) {
        // Dial (rotating with -azimuth so labels stay aligned to true world)
        Canvas(modifier = Modifier.fillMaxSize().rotate(animDial)) {
            val r = min(size.width, size.height) / 2f
            val center = Offset(size.width / 2f, size.height / 2f)

            // Outer ring
            drawCircle(color = emerald, radius = r - 2f, style = Stroke(width = 6f))
            // Inner ring
            drawCircle(color = gold.copy(alpha = 0.5f), radius = r * 0.78f, style = Stroke(width = 2f))

            // Tick marks every 15°
            for (deg in 0 until 360 step 15) {
                val isMajor = deg % 90 == 0
                val tickLen = if (isMajor) 22f else 10f
                val angle = Math.toRadians((deg - 90).toDouble())
                val x1 = center.x + (r - 8f) * cos(angle).toFloat()
                val y1 = center.y + (r - 8f) * sin(angle).toFloat()
                val x2 = center.x + (r - 8f - tickLen) * cos(angle).toFloat()
                val y2 = center.y + (r - 8f - tickLen) * sin(angle).toFloat()
                drawLine(
                    color = if (isMajor) emerald else emerald.copy(alpha = 0.5f),
                    start = Offset(x1, y1),
                    end = Offset(x2, y2),
                    strokeWidth = if (isMajor) 4f else 2f,
                )
            }

            // Cardinal labels
            val labels = listOf(0 to "N", 90 to "E", 180 to "S", 270 to "W")
            labels.forEach { (deg, txt) ->
                val angle = Math.toRadians((deg - 90).toDouble())
                val labelR = r - 50f
                val px = center.x + labelR * cos(angle).toFloat()
                val py = center.y + labelR * sin(angle).toFloat()
                val layout = measurer.measure(txt, style = labelStyle)
                drawText(
                    textLayoutResult = layout,
                    topLeft = Offset(
                        px - layout.size.width / 2f,
                        py - layout.size.height / 2f,
                    ),
                )
            }
        }

        // Qibla needle (rotates by qiblaBearing - azimuth, smoothed)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val r = min(size.width, size.height) / 2f

            rotate(degrees = animNeedle, pivot = Offset(cx, cy)) {
                val needleColor = if (isAligned) gold else emerald
                val tailColor = emerald.copy(alpha = 0.35f)

                // Tail (south end)
                val tail = Path().apply {
                    moveTo(cx, cy + r * 0.6f)
                    lineTo(cx - 18f, cy)
                    lineTo(cx + 18f, cy)
                    close()
                }
                drawPath(tail, color = tailColor)

                // Head (north / Qibla end) — stops short of the Kaaba icon
                val head = Path().apply {
                    moveTo(cx, cy - r * 0.55f)
                    lineTo(cx - 18f, cy)
                    lineTo(cx + 18f, cy)
                    close()
                }
                drawPath(head, color = needleColor)

                // ----- Stylized Kaaba at the tip of the needle -----
                val kaabaCx = cx
                val kaabaCy = cy - r * 0.72f
                val kSize = (r * 0.22f).coerceAtLeast(28f)
                val half = kSize / 2f

                // Glow halo when aligned
                if (isAligned) {
                    drawCircle(
                        color = gold.copy(alpha = 0.30f),
                        radius = kSize * 0.95f,
                        center = Offset(kaabaCx, kaabaCy),
                    )
                }

                // Cube body (black with subtle 3D shading)
                drawRect(
                    color = Color(0xFF101010),
                    topLeft = Offset(kaabaCx - half, kaabaCy - half),
                    size = androidx.compose.ui.geometry.Size(kSize, kSize),
                )
                // Right-side shading for 3D feel
                drawRect(
                    color = Color(0x33000000),
                    topLeft = Offset(kaabaCx + half * 0.4f, kaabaCy - half),
                    size = androidx.compose.ui.geometry.Size(half * 0.6f, kSize),
                )

                // Gold "kiswah" band across upper third
                drawRect(
                    color = gold,
                    topLeft = Offset(kaabaCx - half, kaabaCy - half * 0.55f),
                    size = androidx.compose.ui.geometry.Size(kSize, kSize * 0.16f),
                )
                // Inner thin gold stripe (decorative)
                drawRect(
                    color = gold.copy(alpha = 0.6f),
                    topLeft = Offset(kaabaCx - half, kaabaCy - half * 0.30f),
                    size = androidx.compose.ui.geometry.Size(kSize, 2f),
                )

                // Door (small gold rectangle on lower-right of cube)
                drawRect(
                    color = gold,
                    topLeft = Offset(kaabaCx + half * 0.10f, kaabaCy + half * 0.10f),
                    size = androidx.compose.ui.geometry.Size(kSize * 0.20f, kSize * 0.40f),
                )

                // Outline
                drawRect(
                    color = emerald,
                    topLeft = Offset(kaabaCx - half, kaabaCy - half),
                    size = androidx.compose.ui.geometry.Size(kSize, kSize),
                    style = Stroke(width = 2f),
                )
            }

            // Center hub
            drawCircle(color = emerald, radius = 14f, center = Offset(cx, cy))
            drawCircle(color = gold, radius = 6f, center = Offset(cx, cy))
        }
    }
}

/* ------------------------------- Sub panels ------------------------------- */

@Composable
private fun PermissionPanel(onRequest: () -> Unit) {
    Spacer(Modifier.height(40.dp))
    Icon(Icons.Filled.MyLocation, null, tint = Emerald900, modifier = Modifier.size(56.dp))
    Spacer(Modifier.height(12.dp))
    Text(
        "Location required",
        style = MaterialTheme.typography.titleLarge,
        color = Emerald900,
        fontWeight = FontWeight.Bold,
    )
    Spacer(Modifier.height(6.dp))
    Text(
        "Allow location access to calculate the precise Qibla direction from your position.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Spacer(Modifier.height(20.dp))
    Button(
        onClick = onRequest,
        colors = ButtonDefaults.buttonColors(containerColor = Emerald900, contentColor = Color.White),
    ) { Text("Grant location permission") }
}

@Composable
private fun LoadingPanel() {
    Spacer(Modifier.height(40.dp))
    CircularProgressIndicator(color = Emerald900)
    Spacer(Modifier.height(16.dp))
    Text("Locating you…", color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable
private fun ErrorPanel(title: String, message: String) {
    Spacer(Modifier.height(40.dp))
    Text(title, style = MaterialTheme.typography.titleLarge, color = Emerald900, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(8.dp))
    Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable
private fun InfoRow(bearing: Float, location: android.location.Location?) {
    Surface(
        color = Emerald900.copy(alpha = 0.06f),
        shape = RoundedCornerShape(14.dp),
    ) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(
                "Qibla bearing: ${bearing.toInt()}° from True North",
                color = Emerald900,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
            )
            location?.let {
                Text(
                    "Location: %.4f, %.4f".format(it.latitude, it.longitude),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                )
            }
        }
    }
}

@Composable
private fun CalibrationHint() {
    Surface(
        color = Gold500.copy(alpha = 0.12f),
        shape = RoundedCornerShape(14.dp),
    ) {
        Text(
            "Tip: Move your phone in a figure-8 pattern to calibrate the compass.",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            color = Emerald900,
            fontSize = 12.sp,
        )
    }
}

/* ----------------------------- Compact card ----------------------------- */

/**
 * Small Qibla compass card for the Home screen.
 * Tap navigates to the full [QiblaScreen].
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun QiblaHomeCard(
    onOpenFull: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: QiblaViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val permission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(permission.status.isGranted) {
        viewModel.onPermissionResult(permission.status.isGranted)
    }
    DisposableEffect(Unit) {
        viewModel.start()
        onDispose { viewModel.stop() }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        color = Emerald900,
        onClick = onOpenFull,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Mini compass
            Box(
                modifier = Modifier.size(84.dp),
                contentAlignment = Alignment.Center,
            ) {
                CompassView(
                    azimuth = state.azimuth,
                    needleRotation = state.needleRotation,
                    isAligned = state.isAligned,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Explore, null, tint = Gold500, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Qibla Finder",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    when {
                        !state.sensorsAvailable -> "Compass unavailable"
                        !permission.status.isGranted -> "Tap to allow location"
                        state.location == null -> "Locating you…"
                        state.isAligned -> "You are facing the Qibla"
                        else -> "Qibla ${state.qiblaBearing.toInt()}° • turn ${state.needleRotation.toInt()}°"
                    },
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                )
                Spacer(Modifier.height(6.dp))
                Surface(
                    color = Gold500.copy(alpha = 0.20f),
                    shape = RoundedCornerShape(20.dp),
                ) {
                    Text(
                        "Open Compass",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = Gold500,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

/* --------------------------------- Helpers --------------------------------- */

private fun vibrateOnce(context: Context) {
    val ms = 60L
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vm?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        val v = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v?.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION") v?.vibrate(ms)
        }
    }
}

