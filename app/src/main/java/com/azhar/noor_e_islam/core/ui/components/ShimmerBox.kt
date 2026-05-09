package com.azhar.noor_e_islam.core.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** Modifier-based shimmering placeholder. */
@Composable
fun ShimmerBox(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translate by transition.animateFloat(
        initialValue = -300f, targetValue = 1000f,
        animationSpec = infiniteRepeatable(animation = tween(1200), repeatMode = RepeatMode.Restart),
        label = "translate"
    )
    val brush = Brush.linearGradient(
        colors = listOf(
            Color.LightGray.copy(alpha = 0.3f),
            Color.LightGray.copy(alpha = 0.6f),
            Color.LightGray.copy(alpha = 0.3f),
        ),
        start = androidx.compose.ui.geometry.Offset(translate, 0f),
        end = androidx.compose.ui.geometry.Offset(translate + 300f, 300f)
    )
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(brush)
    ) {
        Box(Modifier.fillMaxSize())
    }
}

