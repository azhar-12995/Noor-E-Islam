package com.azhar.noor_e_islam.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// System default for Latin; Arabic font can be wired via downloadable fonts or assets later.
private val Display = FontFamily.Default
private val Body    = FontFamily.Default

// Reserve Arabic family slot — replace with Amiri/NotoNaskh font family when assets are added.
val ArabicFontFamily: FontFamily = FontFamily.Serif

val NoorTypography = Typography(
    displayLarge = TextStyle(fontFamily = Display, fontWeight = FontWeight.Bold,    fontSize = 40.sp, lineHeight = 48.sp, letterSpacing = (-0.25).sp),
    displayMedium= TextStyle(fontFamily = Display, fontWeight = FontWeight.Bold,    fontSize = 32.sp, lineHeight = 40.sp),
    displaySmall = TextStyle(fontFamily = Display, fontWeight = FontWeight.SemiBold,fontSize = 26.sp, lineHeight = 34.sp),
    headlineLarge= TextStyle(fontFamily = Display, fontWeight = FontWeight.SemiBold,fontSize = 24.sp, lineHeight = 32.sp),
    headlineMedium=TextStyle(fontFamily = Display, fontWeight = FontWeight.SemiBold,fontSize = 20.sp, lineHeight = 28.sp),
    headlineSmall= TextStyle(fontFamily = Display, fontWeight = FontWeight.Medium,  fontSize = 18.sp, lineHeight = 24.sp),
    titleLarge   = TextStyle(fontFamily = Display, fontWeight = FontWeight.SemiBold,fontSize = 20.sp, lineHeight = 28.sp),
    titleMedium  = TextStyle(fontFamily = Display, fontWeight = FontWeight.Medium,  fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.15.sp),
    titleSmall   = TextStyle(fontFamily = Display, fontWeight = FontWeight.Medium,  fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp),
    bodyLarge    = TextStyle(fontFamily = Body,    fontWeight = FontWeight.Normal,  fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.5.sp),
    bodyMedium   = TextStyle(fontFamily = Body,    fontWeight = FontWeight.Normal,  fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.25.sp),
    bodySmall    = TextStyle(fontFamily = Body,    fontWeight = FontWeight.Normal,  fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.4.sp),
    labelLarge   = TextStyle(fontFamily = Body,    fontWeight = FontWeight.Medium,  fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp),
    labelMedium  = TextStyle(fontFamily = Body,    fontWeight = FontWeight.Medium,  fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp),
    labelSmall   = TextStyle(fontFamily = Body,    fontWeight = FontWeight.Medium,  fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp),
)

val ArabicTextStyle = TextStyle(
    fontFamily = ArabicFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 30.sp,
    lineHeight = 56.sp,
    letterSpacing = 0.sp,
)

// Backwards compatibility with template
val Typography = NoorTypography
