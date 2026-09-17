package dev.emmett.bitcoinfacts.android.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.emmett.bitcoinfacts.android.R

/**
 * Task 12.1: new nav-graph start destination, offering "All Bites" (the
 * existing list screen) and "Random Journey" (an unfiltered swipeable
 * journey) as distinct entry points.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    streak: Int,
    onBrowseAll: () -> Unit,
    onRandomJourney: () -> Unit,
    onOpenFavorites: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bitcoin Bites") },
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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(R.drawable.bunny_mascot),
                contentDescription = null, // decorative - the screen title already says "Bitcoin Bites"
                modifier = Modifier.size(120.dp),
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onBrowseAll, modifier = Modifier.fillMaxWidth()) {
                Text("All Bites")
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(onClick = onRandomJourney, modifier = Modifier.fillMaxWidth()) {
                Text("Random Journey")
            }
        }
    }
}
