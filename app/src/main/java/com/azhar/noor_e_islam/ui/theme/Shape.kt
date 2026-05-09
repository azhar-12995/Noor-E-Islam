package com.azhar.noor_e_islam.ui.theme

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val NoorShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small      = RoundedCornerShape(12.dp),
    medium     = RoundedCornerShape(18.dp),
    large      = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

/** Distinctive arch-like shape used for hero cards / splash. */
val IslamicArchShape = RoundedCornerShape(
    topStart = CornerSize(64.dp),
    topEnd = CornerSize(64.dp),
    bottomStart = CornerSize(16.dp),
    bottomEnd = CornerSize(16.dp)
)

