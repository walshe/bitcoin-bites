package dev.emmett.bitcoinfacts.android.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import dev.emmett.bitcoinfacts.shared.model.Fact
import dev.emmett.bitcoinfacts.shared.model.VideoEmbed

/**
 * Sources are kept on every fact (compiler still requires them) but hidden
 * from the UI for now - flip this back on once we decide how we want
 * citations to look/read in-app.
 */
private const val SHOW_SOURCES = false

/** Every fact body ends with this paragraph, per content/facts/README.md's authoring convention. */
private const val ANALOGY_MARKER = "**Analogy:**"

/**
 * Body text (with markdown bold rendering) + sources - shared between
 * [FactDetailScreen] and the Journey screen's per-page card, since both show
 * the same "full detail" content (design.md decision 1 for Journey mode).
 */
@Composable
fun FactBodyAndSources(fact: Fact, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val (mainText, analogyText) = fact.body.splitOutAnalogy()

    Column(modifier = modifier) {
        CategoryAndDifficultyRow(fact)

        val videoUrl = fact.videoUrl
        if (videoUrl != null) {
            Spacer(modifier = Modifier.height(16.dp))
            VideoPlayer(videoUrl = videoUrl, modifier = Modifier.fillMaxWidth())
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(mainText.parseBoldMarkdown(boldColor = MaterialTheme.colorScheme.primary))

        if (analogyText != null) {
            Spacer(modifier = Modifier.height(20.dp))
            Surface(
                color = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = analogyText.parseBoldMarkdown(),
                    modifier = Modifier.padding(16.dp),
                )
            }
        }

        if (SHOW_SOURCES) {
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
}

/**
 * Renders a "video fact"'s embedded player. [videoUrl] is the raw link an author
 * put in `video_url` frontmatter (an X/Twitter status, a YouTube link, ...);
 * [VideoEmbed] resolves it to the actual chromeless-embed URL the WebView loads.
 *
 * This is the one place in the app that makes a network call - see the
 * `INTERNET` permission comment in AndroidManifest.xml. Every other fact stays
 * fully offline; only opening a fact with a `video_url` set reaches the network.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun VideoPlayer(videoUrl: String, modifier: Modifier = Modifier) {
    val embedUrl = remember(videoUrl) { VideoEmbed.resolve(videoUrl) }
    Surface(
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.aspectRatio(16f / 9f),
    ) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    webViewClient = WebViewClient()
                    loadUrl(embedUrl)
                }
            },
            modifier = Modifier.fillMaxSize(),
        )
    }
}

/**
 * Warm glow anchored to the top-left of the viewport, behind a fact's
 * title/metadata. Deliberately a fixed decorative layer, not part of the
 * scrollable content - it stays put as a bit of ambient depth while the text
 * scrolls past it, rather than scrolling away with the title. Biased toward
 * the top-left (rather than centered) since that's where the title and
 * category chips actually sit.
 */
@Composable
fun TopGlow(modifier: Modifier = Modifier) {
    val glowColor = MaterialTheme.colorScheme.primary
    // The box must be taller than the gradient radius, or the fade gets cut off
    // mid-way by the box's own bottom edge - which reads as an abrupt hard line
    // rather than a smooth fade to nothing.
    val radius = 260.dp
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(radius * 1.6f)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        glowColor.copy(alpha = 0.20f),
                        glowColor.copy(alpha = 0.08f),
                        Color.Transparent,
                    ),
                    center = Offset(0f, 0f),
                    radius = with(LocalDensity.current) { radius.toPx() },
                ),
            ),
    )
}

/**
 * Pulls the trailing "**Analogy:** ..." paragraph out of the body so it can be
 * rendered in its own highlighted card. Falls back to (whole body, null) if a
 * fact ever doesn't have one, rather than dropping content or crashing.
 */
private fun String.splitOutAnalogy(): Pair<String, String?> {
    val paragraphs = split("\n\n")
    val analogyIndex = paragraphs.indexOfFirst { it.trimStart().startsWith(ANALOGY_MARKER) }
    if (analogyIndex == -1) return this to null

    val mainText = paragraphs.filterIndexed { index, _ -> index != analogyIndex }.joinToString("\n\n")
    return mainText to paragraphs[analogyIndex]
}
