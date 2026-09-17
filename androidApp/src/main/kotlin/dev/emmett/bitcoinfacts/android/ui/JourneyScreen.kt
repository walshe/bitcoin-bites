package dev.emmett.bitcoinfacts.android.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.emmett.bitcoinfacts.android.data.AppContainer
import dev.emmett.bitcoinfacts.shared.history.JourneyMode
import dev.emmett.bitcoinfacts.taxonomy.Category

/**
 * "Unbounded" for swipe purposes (real page = raw page % fact count), but kept
 * far below Int.MAX_VALUE deliberately - HorizontalPager's internal layout math
 * multiplies page count by page width in pixels, and Int.MAX_VALUE / 2 pages
 * overflowed that Int arithmetic and caused a real ANR (found on-device). This
 * value is already far more swipes than anyone will do in one sitting.
 */
private const val VIRTUAL_PAGE_COUNT = 100_000

/**
 * Task group 12: swipeable one-fact-at-a-time browsing, scoped to [categories]
 * (empty = every fact). In random mode ([mode] not [JourneyMode.GUIDED]),
 * reshuffles and loops forever once the shuffled order is exhausted
 * (design.md decision 13) rather than showing an end state. In guided mode,
 * facts are shown in their canonical `order` (i.e. the order they were
 * authored in, chapter by chapter) instead of shuffled, and that order never
 * reshuffles - looping back to the start after the last fact just replays
 * the same sequence.
 *
 * [mode] also controls resume-position persistence: passing [JourneyMode.GUIDED]
 * or [JourneyMode.RANDOM] remembers the last fact viewed (via
 * [AppContainer.journeyProgressStore]) and resumes there next time the same
 * mode is opened; passing null (the category-filtered "Start Journey" entry
 * point from the list screen) skips persistence entirely, since a filtered
 * journey's scope varies run to run and isn't a single "position" worth
 * remembering.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JourneyScreen(
    container: AppContainer,
    categories: Set<Category>,
    favoriteIds: Set<String>,
    onToggleFavorite: (String) -> Unit,
    onBack: () -> Unit,
    mode: JourneyMode? = null,
) {
    val guided = mode == JourneyMode.GUIDED

    var journeyFacts by remember(categories, mode) {
        val filtered = container.factRepository.filterByCategories(categories)
        val resumeFact = mode?.let { container.journeyProgressStore.lastFactId(it) }
            ?.let { savedId -> filtered.find { it.id == savedId } }
        val ordered = when {
            guided -> filtered
            resumeFact != null -> listOf(resumeFact) + (filtered - resumeFact).shuffled()
            else -> filtered.shuffled()
        }
        mutableStateOf(ordered)
    }

    if (journeyFacts.isEmpty()) {
        EmptyJourneyMessage(onBack)
        return
    }

    // Only guided mode needs a non-zero starting page - random/filtered modes
    // already put the resume fact (if any) at index 0 above.
    val initialPage = remember(categories, mode) {
        if (guided) {
            val savedId = mode?.let { container.journeyProgressStore.lastFactId(it) }
            savedId?.let { id -> journeyFacts.indexOfFirst { it.id == id } }?.takeIf { it >= 0 } ?: 0
        } else {
            0
        }
    }
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { VIRTUAL_PAGE_COUNT })
    var lastCycle by remember { mutableStateOf(0) }

    if (!guided) {
        LaunchedEffect(pagerState.currentPage, journeyFacts.size) {
            val cycle = pagerState.currentPage / journeyFacts.size
            if (cycle > lastCycle) {
                lastCycle = cycle
                journeyFacts = journeyFacts.shuffled()
            }
        }
    }

    val currentFact = journeyFacts[pagerState.currentPage % journeyFacts.size]

    // Subtle one-off affordance: it's not obvious this screen is swipeable, so
    // nudge with a small animated hint until the user actually swipes once.
    var hasSwiped by remember(initialPage) { mutableStateOf(false) }
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage != initialPage) hasSwiped = true
    }

    if (mode != null) {
        LaunchedEffect(currentFact.id, mode) {
            container.journeyProgressStore.setLastFactId(mode, currentFact.id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (guided) "Guided ₿ites" else "Journey") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onToggleFavorite(currentFact.id) }) {
                        val isFavorite = currentFact.id in favoriteIds
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = if (isFavorite) "Unfavorite" else "Favorite",
                        )
                    }
                },
            )
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                val fact = journeyFacts[page % journeyFacts.size]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                ) {
                    Text(fact.title, style = MaterialTheme.typography.headlineMedium)
                    Text(
                        text = fact.categories.joinToString(" · ") { it.displayName() },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp),
                    )
                    FactBodyAndSources(fact)
                }
            }
            AnimatedVisibility(
                visible = !hasSwiped,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp),
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                SwipeHint()
            }
        }
    }
}

/**
 * Small pill nudging left, hinting the screen above it can be swiped. Points
 * and moves left deliberately - that's the actual finger motion needed to
 * advance to the next fact, not the direction "forward" might suggest.
 */
@Composable
private fun SwipeHint() {
    val offset by rememberInfiniteTransition(label = "swipeHint").animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "swipeHintOffset",
    )
    Row(
        modifier = Modifier
            .offset(x = offset.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                shape = RoundedCornerShape(50),
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.KeyboardArrowLeft,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = "Swipe",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmptyJourneyMessage(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Journey") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "No facts match this selection yet.",
                modifier = Modifier.padding(32.dp),
            )
        }
    }
}
