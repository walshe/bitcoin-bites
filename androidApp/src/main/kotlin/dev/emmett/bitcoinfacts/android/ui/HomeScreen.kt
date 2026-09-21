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
                HomeButtonContent(Icons.Filled.List, "All Bites")
            }

            if (continueFact != null) {
                Spacer(modifier = Modifier.height(20.dp))
                ContinueReadingCard(title = continueFact.title, onClick = onGuidedJourney)
            }
        }
    }
}

/**
 * Icon + label for a home screen button. The icon sits at the trailing edge
 * of a fixed-width leading slot (close to the text, not pinned to the far
 * left edge), and the label is centered in the remaining space with a
 * matching trailing spacer so it stays centered in the button overall.
 * Every icon lands at the same x position across all four buttons, but
 * pulled in toward the middle rather than hugging the button's left edge.
 */
@Composable
private fun HomeButtonContent(icon: ImageVector, label: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.width(92.dp), contentAlignment = Alignment.CenterEnd) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            Text(label, maxLines = 1)
        }
        Spacer(modifier = Modifier.width(100.dp))
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
