package dev.emmett.bitcoinfacts.android.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.emmett.bitcoinfacts.android.data.AppContainer
import dev.emmett.bitcoinfacts.shared.model.Fact
import dev.emmett.bitcoinfacts.taxonomy.Category

/**
 * Task 6.1 (nav shell entry) + 6.3 (browse) + 6.4 (category filter) + 7.1
 * (favorite toggle) + 7.3 (streak display). Built together since they're all
 * part of the same screen - splitting the commit history by task group would
 * mean revisiting this file three times for no real benefit.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FactListScreen(
    container: AppContainer,
    selectedCategories: Set<Category>,
    onToggleCategory: (Category) -> Unit,
    favoriteIds: Set<String>,
    onToggleFavorite: (String) -> Unit,
    streak: Int,
    onFactClick: (String) -> Unit,
    onOpenFavorites: () -> Unit,
    onBack: () -> Unit,
    onStartJourney: () -> Unit,
) {
    val facts = container.factRepository.filterByCategories(selectedCategories)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bitcoin Bites") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (streak > 0) {
                        Text(
                            text = "🔥 $streak",
                            modifier = Modifier.padding(end = 8.dp),
                        )
                    }
                    IconButton(onClick = onOpenFavorites) {
                        Icon(Icons.Filled.Favorite, contentDescription = "Favorites")
                    }
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = onStartJourney) {
                Text("Start Journey")
            }
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            ) {
                items(Category.entries.toList()) { category ->
                    FilterChip(
                        selected = category in selectedCategories,
                        onClick = { onToggleCategory(category) },
                        label = { Text(category.displayName()) },
                    )
                }
            }

            LazyColumn {
                items(facts, key = { it.id }) { fact ->
                    FactRow(
                        fact = fact,
                        isFavorite = fact.id in favoriteIds,
                        onClick = { onFactClick(fact.id) },
                        onToggleFavorite = { onToggleFavorite(fact.id) },
                    )
                    Divider()
                }
            }
        }
    }
}

@Composable
private fun FactRow(
    fact: Fact,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        headlineContent = { Text(fact.title) },
        supportingContent = {
            Text(fact.categories.joinToString(" · ") { it.displayName() })
        },
        trailingContent = {
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = if (isFavorite) "Unfavorite" else "Favorite",
                )
            }
        },
    )
}
