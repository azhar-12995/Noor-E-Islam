package com.azhar.noor_e_islam.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.azhar.noor_e_islam.R
import com.azhar.noor_e_islam.core.navigation.Route
import com.azhar.noor_e_islam.core.ui.components.GeometricPatternBg
import com.azhar.noor_e_islam.ui.theme.Gold500
import com.azhar.noor_e_islam.ui.theme.NoorGradients
import kotlinx.coroutines.delay

@androidx.compose.runtime.Composable
fun SplashScreen(
    onNavigate: (String) -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val state by viewModel.destination.collectAsState()

    LaunchedEffect(state) {
        delay(1400)
        when (val s = state) {
            is SplashDestination.Onboarding -> onNavigate(Route.Onboarding.route)
            is SplashDestination.Auth       -> onNavigate(Route.Login.route)
            is SplashDestination.Home       -> onNavigate(Route.Home.route)
            is SplashDestination.Admin      -> onNavigate(Route.AdminDashboard.route)
            null -> Unit
        }
    }

    val moonAlpha = remember { Animatable(0.4f) }
    LaunchedEffect(Unit) {
        moonAlpha.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(1400, easing = LinearEasing), RepeatMode.Reverse)
        )
    }

    Box(
        modifier = Modifier.fillMaxSize().background(NoorGradients.Night),
        contentAlignment = Alignment.Center
    ) {
        GeometricPatternBg(modifier = Modifier.fillMaxSize())

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Crescent moon glow
            Box(modifier = Modifier.size(140.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val r = size.minDimension / 2
                    val center = Offset(size.width / 2, size.height / 2)
                    drawCircle(color = Gold500.copy(alpha = 0.1f * moonAlpha.value), radius = r, center = center)
                    drawCircle(color = Gold500.copy(alpha = 0.4f * moonAlpha.value), radius = r * 0.75f, style = Stroke(width = 2f), center = center)
                    // Crescent — main + offset cutout
                    drawCircle(color = Gold500, radius = r * 0.55f, center = center)
                    drawCircle(color = androidx.compose.ui.graphics.Color.Transparent, radius = r * 0.5f, center = center.copy(x = center.x + r * 0.18f), blendMode = androidx.compose.ui.graphics.BlendMode.Clear)
                }
            }
            Spacer(Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.app_name),
                color = Gold500,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.app_tagline),
                color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.85f),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

