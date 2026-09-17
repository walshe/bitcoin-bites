package dev.emmett.bitcoinfacts.android.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
    val fact = container.factRepository.findById(factId) ?: return
    val related = container.factRepository.relatedFacts(fact)
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(fact.title) },
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
                        )
                    }
                    IconButton(onClick = { shareFact(context, fact) }) {
                        Icon(Icons.Filled.Share, contentDescription = "Share")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(fact.body.parseBoldMarkdown())

            Spacer(modifier = Modifier.height(24.dp))
            Text("Sources", style = MaterialTheme.typography.titleMedium)
            fact.sources.forEach { source ->
                Text(
                    text = source.title,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clickable {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(source.url)))
                        },
                )
            }

            if (related.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Related facts", style = MaterialTheme.typography.titleMedium)
                related.forEach { relatedFact ->
                    RelatedFactCard(relatedFact, onClick = { onRelatedFactClick(relatedFact.id) })
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

private fun shareFact(context: android.content.Context, fact: Fact) {
    val excerpt = fact.body.lineSequence().firstOrNull { it.isNotBlank() } ?: ""
    val shareText = "${fact.title}\n\n$excerpt"
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    context.startActivity(Intent.createChooser(intent, "Share fact"))
}
