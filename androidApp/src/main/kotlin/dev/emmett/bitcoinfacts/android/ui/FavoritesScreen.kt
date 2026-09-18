package dev.emmett.bitcoinfacts.android.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.emmett.bitcoinfacts.android.R
import dev.emmett.bitcoinfacts.android.data.AppContainer

/** Task 7.2: lists only currently-favorited facts. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    container: AppContainer,
    favoriteIds: Set<String>,
    onBack: () -> Unit,
    onFactClick: (String) -> Unit,
) {
    // Use the repository's own order (the canonical `order` field facts.json is compiled
    // by - see design.md decision 16) rather than favoriteIds' iteration order, so Favorites
    // reads in the same sequence as Browse All/Journey instead of a third, undocumented sort.
    val favoriteFacts = container.factRepository.all().filter { it.id in favoriteIds }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Favorites",
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        if (favoriteFacts.isEmpty()) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp),
                ) {
                    Image(
                        painter = painterResource(R.drawable.bunny_mascot),
                        contentDescription = null, // decorative
                        modifier = Modifier.size(96.dp),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No favorites yet - tap the heart on any fact to save it here.",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(favoriteFacts, key = { it.id }) { fact ->
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onFactClick(fact.id) },
                        headlineContent = { Text(fact.title) },
                        supportingContent = {
                            Text(fact.categories.joinToString(" · ") { it.displayName() })
                        },
                    )
                    Divider()
                }
            }
        }
    }
}
