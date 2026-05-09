package com.azhar.noor_e_islam.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/* Brushes used across the app for premium Islamic aesthetics. */

object NoorGradients {
    val Emerald: Brush
        get() = Brush.verticalGradient(
            colors = listOf(Emerald800, Emerald600, Emerald500)
        )

    val EmeraldDeep: Brush
        get() = Brush.verticalGradient(
            colors = listOf(Emerald900, Emerald700)
        )

    val Gold: Brush
        get() = Brush.horizontalGradient(
            colors = listOf(Gold700, Gold500, Gold300)
        )

    val Night: Brush
        get() = Brush.verticalGradient(
            colors = listOf(Midnight, Emerald900, Charcoal)
        )

    val IvorySoft: Brush
        get() = Brush.verticalGradient(
            colors = listOf(Ivory, IvoryDim)
        )
}

