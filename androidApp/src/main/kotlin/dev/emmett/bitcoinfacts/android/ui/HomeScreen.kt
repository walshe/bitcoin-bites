package dev.emmett.bitcoinfacts.android.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.emmett.bitcoinfacts.android.R
import dev.emmett.bitcoinfacts.android.data.AppContainer
import dev.emmett.bitcoinfacts.shared.history.JourneyMode

/**
 * The Videos entry point is hidden for now - video facts aren't ready to
 * ship. It's the only way to reach them (FactRepository keeps video facts
 * out of All Bites and the journeys), so flip this back on to release them.
 */
private const val SHOW_VIDEOS = false

/**
 * Task 12.1: new nav-graph start destination, offering "Guided Bites" (an
 * unfiltered swipeable journey in canonical `order` - the order facts were
 * authored in, chapter by chapter), "Random Bites" (the same swipeable
 * journey, shuffled) and "All Bites" (the existing list screen) as distinct
 * entry points, in that display order.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    container: AppContainer,
    streak: Int,
    onBrowseAll: () -> Unit,
    onGuidedJourney: () -> Unit,
    onRandomJourney: () -> Unit,
    onOpenFavorites: () -> Unit,
    onBrowseVideos: () -> Unit,
) {
    val continueFact = remember {
        container.journeyProgressStore.lastFactId(JourneyMode.GUIDED)?.let { container.factRepository.findById(it) }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Bitcoin Bites",
                actions = {
                    if (streak > 0) {
                        StreakBadge(streak)
                    }
                    IconButton(onClick = onOpenFavorites) {
                        Icon(Icons.Filled.Favorite, contentDescription = "Favorites")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                // The mascot plus four entry points overflow a short screen (or a
                // normal one at a large display/font scale), which silently cut off
                // the "Continue reading" card with no way to reach it.
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Box(contentAlignment = Alignment.Center) {
                MascotGlow()
                Image(
                    painter = painterResource(R.drawable.bunny_mascot),
                    contentDescription = null, // decorative - the screen title already says "Bitcoin Bites"
                    modifier = Modifier.size(200.dp),
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Bitcoin Knowledge — one bite at a time",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = onGuidedJourney, modifier = Modifier.fillMaxWidth()) {
                HomeButtonContent(Icons.Filled.PlayArrow, "Guided Bites")
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(onClick = onRandomJourney, modifier = Modifier.fillMaxWidth()) {
                HomeButtonContent(Icons.Filled.Refresh, "Random Bites")
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(onClick = onBrowseAll, modifier = Modifier.fillMaxWidth()) {
                HomeButtonContent(Icons.Filled.Menu, "All Bites")
            }
            if (SHOW_VIDEOS) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(onClick = onBrowseVideos, modifier = Modifier.fillMaxWidth()) {
                    HomeButtonContent(Icons.Filled.List, "Videos")
                }
            }

            if (continueFact != null) {
                Spacer(modifier = Modifier.height(20.dp))
                ContinueReadingCard(title = continueFact.title, onClick = onGuidedJourney)
            }
        }
    }
}

/**
 * Icon + label for a home screen button.
 *
 * The two are positioned independently rather than laid out in a row: the
 * label is centered in the button and the icon is pinned a fixed distance
 * from the start. That keeps every button's icon on the same x - a row would
 * shift it by the label's width - while the label, being centered rather than
 * sharing a row, is never squeezed by the icon's slot.
 *
 * An earlier version did use a row with a fixed 92dp leading slot and a 100dp
 * trailing spacer. That consumed 200dp before the label was measured, leaving
 * roughly 64dp on a 360dp phone, and `maxLines = 1` silently clipped the rest:
 * "Guided Bites" rendered as "Guided". Keeping the label out of the row's
 * width budget is what stops that recurring at any width or font scale.
 */
@Composable
private fun HomeButtonContent(icon: ImageVector, label: String) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, maxLines = 1)
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
                .size(20.dp),
        )
    }
}

/** One-time bouncy pop-in for the streak counter on screen load, instead of sitting static. */
@Composable
private fun StreakBadge(streak: Int) {
    var settled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { settled = true }
    val scale by animateFloatAsState(
        targetValue = if (settled) 1f else 0.6f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "streakPulse",
    )
    Text(
        text = "🔥 $streak",
        modifier = Modifier
            .padding(end = 8.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale },
    )
}

/** Very subtle warm halo centered behind the mascot - purely decorative. */
@Composable
private fun MascotGlow() {
    val glowColor = MaterialTheme.colorScheme.primary
    Box(
        modifier = Modifier
            .size(260.dp)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        glowColor.copy(alpha = 0.22f),
                        glowColor.copy(alpha = 0.08f),
                        Color.Transparent,
                    ),
                ),
            ),
    )
}

/** Shortcut back into Guided Bites, showing the title of the fact it'll resume on. */
@Composable
private fun ContinueReadingCard(title: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Continue reading",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Icon(
                imageVector = Icons.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
