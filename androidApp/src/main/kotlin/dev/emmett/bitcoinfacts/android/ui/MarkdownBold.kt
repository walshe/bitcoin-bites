package dev.emmett.bitcoinfacts.android.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

private val BOLD_MARKER = Regex("\\*\\*(.+?)\\*\\*")

/**
 * Fact bodies use plain **bold** markdown for emphasis (see content/facts/README.md);
 * this renders just that one construct rather than pulling in a full markdown
 * library for a single inline style.
 *
 * [boldColor] defaults to [Color.Unspecified], which leaves bold spans using
 * whatever color the surrounding [androidx.compose.material3.Text] already has
 * (needed for the Analogy card, where bold text must stay readable against an
 * orange background, not turn orange itself). Pass an explicit color - e.g.
 * the brand orange - to make bold terms stand out from plain body text.
 */
fun String.parseBoldMarkdown(boldColor: Color = Color.Unspecified): AnnotatedString = buildAnnotatedString {
    var cursor = 0
    for (match in BOLD_MARKER.findAll(this@parseBoldMarkdown)) {
        append(this@parseBoldMarkdown.substring(cursor, match.range.first))
        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = boldColor)) {
            append(match.groupValues[1])
        }
        cursor = match.range.last + 1
    }
    append(this@parseBoldMarkdown.substring(cursor))
}
