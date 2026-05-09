package com.azhar.noor_e_islam.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import com.azhar.noor_e_islam.ui.theme.Gold500

/**
 * Lightweight Islamic geometric pattern (interlocking 8-point stars + diamonds).
 * Drawn purely on Canvas — no asset dependency.
 */
@Composable
fun GeometricPatternBg(
    modifier: Modifier = Modifier,
    color: Color = Gold500.copy(alpha = 0.10f),
) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val step = 64f
            val w = size.width
            val h = size.height
            val stroke = Stroke(width = 1.2f)
            var y = 0f
            while (y < h + step) {
                var x = 0f
                while (x < w + step) {
                    // 8-point star approximated by overlapping diamonds
                    val c = Offset(x, y)
                    val r = step / 2
                    val pts = listOf(
                        Offset(c.x - r, c.y), Offset(c.x, c.y - r),
                        Offset(c.x + r, c.y), Offset(c.x, c.y + r),
                    )
                    for (i in pts.indices) {
                        val a = pts[i]
                        val b = pts[(i + 1) % pts.size]
                        drawLine(color, a, b, stroke.width)
                    }
                    val rs = r * 0.7f
                    val pts2 = listOf(
                        Offset(c.x - rs, c.y - rs), Offset(c.x + rs, c.y - rs),
                        Offset(c.x + rs, c.y + rs), Offset(c.x - rs, c.y + rs),
                    )
                    for (i in pts2.indices) {
                        val a = pts2[i]
                        val b = pts2[(i + 1) % pts2.size]
                        drawLine(color, a, b, stroke.width)
                    }
                    x += step
                }
                y += step
            }
        }
    }
}

