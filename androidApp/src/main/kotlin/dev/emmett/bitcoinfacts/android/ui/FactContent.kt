package dev.emmett.bitcoinfacts.android.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.emmett.bitcoinfacts.shared.model.Fact

/**
 * Body text (with markdown bold rendering) + sources - shared between
 * [FactDetailScreen] and the Journey screen's per-page card, since both show
 * the same "full detail" content (design.md decision 1 for Journey mode).
 */
@Composable
fun FactBodyAndSources(fact: Fact, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(modifier = modifier) {
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
    }
}
