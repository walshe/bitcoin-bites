package dev.emmett.bitcoinfacts.android.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.emmett.bitcoinfacts.android.data.AppContainer
import dev.emmett.bitcoinfacts.shared.model.Fact

/**
 * "All Videos" - the video-fact counterpart to [FactListScreen]'s "All Bites".
 * Deliberately simpler: no category filter row and no "Browse One by One" FAB,
 * since the swipeable journey isn't built to render a video embed (see
 * FactRepository.filterByCategories, which excludes video facts for the same
 * reason). Just a flat list, sourced from [AppContainer.factRepository]'s
 * [dev.emmett.bitcoinfacts.shared.FactRepository.videoFacts].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoListScreen(
    container: AppContainer,
    favoriteIds: Set<String>,
    onToggleFavorite: (String) -> Unit,
    onVideoClick: (String) -> Unit,
    onBack: () -> Unit,
) {
    val videos = container.factRepository.videoFacts()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "All Videos",
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Text(
                text = "${videos.size} video" + if (videos.size == 1) "" else "s",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            )

            LazyColumn {
                items(videos, key = { it.id }) { fact ->
                    VideoRow(
                        fact = fact,
                        isFavorite = fact.id in favoriteIds,
                        onClick = { onVideoClick(fact.id) },
                        onToggleFavorite = { onToggleFavorite(fact.id) },
                    )
                    Divider()
                }
            }
        }
    }
}

@Composable
private fun VideoRow(
    fact: Fact,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        leadingContent = {
            Icon(Icons.Filled.PlayArrow, contentDescription = null)
        },
        headlineContent = { Text(fact.title) },
        supportingContent = {
            CategoryAndDifficultyRow(fact, modifier = Modifier.padding(top = 4.dp))
        },
        trailingContent = {
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = if (isFavorite) "Unfavorite" else "Favorite",
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else LocalContentColor.current,
                )
            }
        },
    )
}
