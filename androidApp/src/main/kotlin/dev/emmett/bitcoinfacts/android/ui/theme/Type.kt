package dev.emmett.bitcoinfacts.android.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/** Bold, heavy-weight headlines (matching the reference's bold display type), Material defaults otherwise. */
val AppTypography = Typography().let { base ->
    base.copy(
        headlineLarge = base.headlineLarge.copy(fontWeight = FontWeight.Black, fontFamily = FontFamily.SansSerif),
        headlineMedium = base.headlineMedium.copy(fontWeight = FontWeight.Black, fontFamily = FontFamily.SansSerif),
        titleLarge = base.titleLarge.copy(fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.SansSerif),
        titleMedium = base.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.SansSerif),
        labelSmall = base.labelSmall.copy(letterSpacing = 0.8.sp),
    )
}
