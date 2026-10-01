package com.sastellarsolutions.qaclothingfactory.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.sastellarsolutions.qaclothingfactory.R


// ============================================================
// CINZEL
// Used for branding and important headings.
// ============================================================

val CinzelFontFamily = FontFamily(

    Font(
        resId = R.font.cinzel_regular,
        weight = FontWeight.Normal
    ),

    Font(
        resId = R.font.cinzel_semibold,
        weight = FontWeight.SemiBold
    )
)


// ============================================================
// MONTSERRAT
// Used for normal application text.
// ============================================================

val MontserratFontFamily = FontFamily(

    Font(
        resId = R.font.montserrat_regular,
        weight = FontWeight.Normal
    ),

    Font(
        resId = R.font.montserrat_medium,
        weight = FontWeight.Medium
    ),

    Font(
        resId = R.font.montserrat_semibold,
        weight = FontWeight.SemiBold
    )
)


// ============================================================
// QA CLOTHING FACTORY TYPOGRAPHY
// ============================================================

val Typography = Typography(

    // Large screen headings
    headlineLarge = TextStyle(
        fontFamily = CinzelFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 30.sp,
        letterSpacing = 1.sp
    ),

    // Medium headings
    headlineMedium = TextStyle(
        fontFamily = CinzelFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 26.sp,
        letterSpacing = 0.8.sp
    ),

    // Smaller headings
    headlineSmall = TextStyle(
        fontFamily = CinzelFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        letterSpacing = 0.5.sp
    ),

    // Normal large text
    bodyLarge = TextStyle(
        fontFamily = MontserratFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),

    // Normal text
    bodyMedium = TextStyle(
        fontFamily = MontserratFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),

    // Small text
    bodySmall = TextStyle(
        fontFamily = MontserratFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),

    // Buttons and other labels
    labelLarge = TextStyle(
        fontFamily = MontserratFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    ),

    labelMedium = TextStyle(
        fontFamily = MontserratFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    )
)