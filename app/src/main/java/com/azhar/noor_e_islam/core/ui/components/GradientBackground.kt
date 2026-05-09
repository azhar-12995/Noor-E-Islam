package com.azhar.noor_e_islam.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.azhar.noor_e_islam.ui.theme.NoorGradients

/** Full-bleed gradient background. */
@Composable
fun GradientBackground(
    brush: Brush = NoorGradients.Emerald,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier.fillMaxSize().background(brush)) { content() }
}

