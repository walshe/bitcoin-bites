package dev.emmett.bitcoinfacts.android.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.emmett.bitcoinfacts.android.data.AppContainer
import dev.emmett.bitcoinfacts.shared.model.Fact

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FactDetailScreen(
    container: AppContainer,
    factId: String,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBack: () -> Unit,
    onRelatedFactClick: (String) -> Unit,
) {
    val fact = container.factRepository.findById(factId)
    if (fact == null) {
        // A stale fact id (e.g. a pending notification pointing at a fact removed in a
        // content update) must still land on a screen with a way back, not a blank one.
        Scaffold(
            topBar = {
                AppTopBar(
                    title = "Fact not found",
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
                    text = "This fact is no longer available.",
                    modifier = Modifier.padding(32.dp),
                )
            }
        }
        return
    }
    val related = container.factRepository.relatedFacts(fact)
    val context = LocalContext.current

    Scaffold(
        topBar = {
            AppTopBar(
                title = fact.title,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = if (isFavorite) "Unfavorite" else "Favorite",
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary else LocalContentColor.current,
                        )
                    }
                    IconButton(onClick = { shareFact(context, fact) }) {
                        Icon(Icons.Filled.Share, contentDescription = "Share")
                    }
                },
            )
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            TopGlow()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                FactBodyAndSources(fact)

                if (related.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Related bites", style = MaterialTheme.typography.titleMedium)
                    related.forEach { relatedFact ->
                        RelatedFactCard(relatedFact, onClick = { onRelatedFactClick(relatedFact.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun RelatedFactCard(fact: Fact, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(top = 8.dp)
            .clickable(onClick = onClick),
    ) {
        Text(
            text = fact.title,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(12.dp),
        )
    }
}

/**
 * `fact.body` already contains the Analogy paragraph inline (the highlighted
 * card in [FactBodyAndSources] is a pure rendering trick, not a real split -
 * see design.md for fact-sharing) and is already reflowed at compile time
 * ([dev.emmett.bitcoinfacts.compiler.FrontmatterParser]'s `normalizeBody`),
 * so sharing it as-is needs nothing beyond stripping the `**bold**` markers,
 * which only make sense for the app's own on-screen rendering.
 */
private val BOLD_MARKER = Regex("\\*\\*(.+?)\\*\\*")

private fun String.stripBoldMarkdown(): String = BOLD_MARKER.replace(this) { it.groupValues[1] }

private const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=dev.emmett.bitcoinfacts.android"

private fun shareFact(context: android.content.Context, fact: Fact) {
    val shareText = "${fact.title}\n\n${fact.body.stripBoldMarkdown()}\n\n— Shared from Bitcoin Bites\n$PLAY_STORE_URL"
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    context.startActivity(Intent.createChooser(intent, "Share fact"))
}
