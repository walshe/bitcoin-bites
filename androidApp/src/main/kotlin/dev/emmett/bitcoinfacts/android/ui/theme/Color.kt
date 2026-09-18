package dev.emmett.bitcoinfacts.android.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Task 6.2: visual identity - a fast, deliberately simple palette (design.md:
 * "pick a palette/typography quickly, iterate visually rather than over-planning
 * here"). Dark-first, with Bitcoin-orange as the sole accent color.
 */
val BitcoinOrange = Color(0xFFF7931A)
val BitcoinOrangeDim = Color(0xFFC9740A)
/** Lightened tint of [BitcoinOrange], used as the highlight end of the primary button's glossy gradient. */
val BitcoinOrangeLight = Color(0xFFFFB74D)

val AppBackground = Color(0xFF121212)
val AppSurface = Color(0xFF1E1E1E)
val AppSurfaceVariant = Color(0xFF2A2A2A)

val TextPrimary = Color(0xFFF5F5F5)
val TextSecondary = Color(0xFFB0B0B0)

val ErrorRed = Color(0xFFCF6679)

/** Difficulty indicator color for "beginner" facts; "advanced" reuses [ErrorRed], "intermediate" reuses [BitcoinOrange]. */
val BeginnerGreen = Color(0xFF6FCF97)
