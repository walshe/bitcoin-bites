package dev.emmett.bitcoinfacts.android.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
 * (empty = every fact). Reshuffles and loops forever once the shuffled order is
 * exhausted (design.md decision 13) rather than showing an end state.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JourneyScreen(
    container: AppContainer,
    categories: Set<Category>,
    favoriteIds: Set<String>,
    onToggleFavorite: (String) -> Unit,
    onBack: () -> Unit,
) {
    var shuffledFacts by remember(categories) {
        mutableStateOf(container.factRepository.filterByCategories(categories).shuffled())
    }

    if (shuffledFacts.isEmpty()) {
        EmptyJourneyMessage(onBack)
        return
    }

    val pagerState = rememberPagerState(pageCount = { VIRTUAL_PAGE_COUNT })
    var lastCycle by remember { mutableStateOf(0) }

    LaunchedEffect(pagerState.currentPage, shuffledFacts.size) {
        val cycle = pagerState.currentPage / shuffledFacts.size
        if (cycle > lastCycle) {
            lastCycle = cycle
            shuffledFacts = shuffledFacts.shuffled()
        }
    }

    val currentFact = shuffledFacts[pagerState.currentPage % shuffledFacts.size]

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Journey") },
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
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize().padding(padding),
        ) { page ->
            val fact = shuffledFacts[page % shuffledFacts.size]
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
