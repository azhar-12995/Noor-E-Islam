package com.azhar.noor_e_islam.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.azhar.noor_e_islam.ui.theme.Gold500

/** Premium rounded card with subtle gold accent border + soft elevation. */
@Composable
fun IslamicCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    showBorder: Boolean = true,
    content: @Composable () -> Unit,
) {
    val cardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor   = MaterialTheme.colorScheme.onSurface,
    )
    val border = if (showBorder) BorderStroke(0.7.dp, Gold500.copy(alpha = 0.45f)) else null
    val shape  = RoundedCornerShape(20.dp)
    val elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)

    if (onClick != null) {
        Card(onClick = onClick, modifier = modifier, colors = cardColors, shape = shape, border = border, elevation = elevation) {
            androidx.compose.foundation.layout.Column(Modifier.padding(16.dp)) { content() }
        }
    } else {
        Card(modifier = modifier, colors = cardColors, shape = shape, border = border, elevation = elevation) {
            androidx.compose.foundation.layout.Column(Modifier.padding(16.dp)) { content() }
        }
    }
}

