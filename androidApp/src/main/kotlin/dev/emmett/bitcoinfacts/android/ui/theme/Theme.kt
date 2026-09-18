package dev.emmett.bitcoinfacts.android.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AppDarkColorScheme = darkColorScheme(
    primary = BitcoinOrange,
    onPrimary = AppBackground, // dark text/icons on top of the orange accent, for contrast
    // Without these two, M3 components that default to *Container roles (FAB,
    // selected FilterChip, etc.) fall back to the stock Material purple instead
    // of the app's orange - found via ExtendedFloatingActionButton rendering
    // purple with no color ever set explicitly for it in this codebase.
    primaryContainer = BitcoinOrangeDim,
    onPrimaryContainer = AppBackground,
    secondary = BitcoinOrangeDim,
    secondaryContainer = BitcoinOrangeDim,
    onSecondaryContainer = AppBackground,
    background = AppBackground,
    onBackground = TextPrimary,
    surface = AppSurface,
    onSurface = TextPrimary,
    surfaceVariant = AppSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
)

// v1 is dark-first (matches the reference style); a light scheme is provided so
// the app doesn't look broken if a user's system theme forces light mode, but
// isn't a design priority - dark stays the default look regardless of system theme.
private val AppLightColorScheme = lightColorScheme(
    primary = BitcoinOrangeDim,
    secondary = BitcoinOrange,
    error = ErrorRed,
)

@Composable
fun BitcoinFactsTheme(
    useDarkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (useDarkTheme) AppDarkColorScheme else AppLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content,
    )
}
