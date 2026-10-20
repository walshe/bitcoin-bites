package dev.emmett.bitcoinfacts.web

import dev.emmett.bitcoinfacts.taxonomy.Category

/** Every fact body ends with this paragraph, per content/facts/README.md's authoring convention. */
private const val ANALOGY_MARKER = "**Analogy:**"

private val BOLD_MARKER = Regex("\\*\\*(.+?)\\*\\*")
private val WHITESPACE = Regex("\\s+")

/**
 * Escapes text for use in HTML element content and double- or single-quoted
 * attribute values. Every piece of fact content goes through this before it
 * reaches a page (design.md decision 2).
 */
fun html(text: String): String = buildString(text.length) {
    for (c in text) {
        when (c) {
            '&' -> append("&amp;")
            '<' -> append("&lt;")
            '>' -> append("&gt;")
            '"' -> append("&quot;")
            '\'' -> append("&#39;")
            else -> append(c)
        }
    }
}

/**
 * Renders the one inline markdown construct fact bodies use, `**bold**`, as
 * `<strong>` - the web counterpart of androidApp's `parseBoldMarkdown()`.
 * Escapes first, so the only markup in the output is the `<strong>` tags
 * added here (`*` is never touched by escaping, so the markers survive it).
 */
fun boldToHtml(text: String): String =
    BOLD_MARKER.replace(html(text)) { "<strong>${it.groupValues[1]}</strong>" }

/** Main text paragraphs plus the separate analogy paragraph, if the body has one. */
data class FormattedBody(val paragraphs: List<String>, val analogy: String?)

/**
 * Same split as androidApp's `FactContent.kt` `splitOutAnalogy()`: paragraphs on
 * blank lines, with the `**Analogy:**` paragraph pulled out so it can be shown
 * in its own highlighted block. A body without one keeps every paragraph.
 */
fun splitBody(body: String): FormattedBody {
    val paragraphs = body.split("\n\n").map { it.trim() }.filter { it.isNotEmpty() }
    val analogyIndex = paragraphs.indexOfFirst { it.startsWith(ANALOGY_MARKER) }
    if (analogyIndex == -1) return FormattedBody(paragraphs, null)
    return FormattedBody(
        paragraphs = paragraphs.filterIndexed { index, _ -> index != analogyIndex },
        analogy = paragraphs[analogyIndex],
    )
}

/** The body as `<p>` elements plus an analogy `<aside>`, all content escaped. */
fun bodyToHtml(body: String): String {
    val formatted = splitBody(body)
    return buildString {
        formatted.paragraphs.forEach { append("<p>").append(boldToHtml(it)).append("</p>\n") }
        formatted.analogy?.let {
            append("<aside class=\"analogy\"><p>").append(boldToHtml(it)).append("</p></aside>\n")
        }
    }
}

/** Plain text with `**` markers removed and paragraph breaks kept - the share text body, as on Android. */
fun stripBold(text: String): String = BOLD_MARKER.replace(text) { it.groupValues[1] }

/**
 * A single-line plain-text excerpt for meta descriptions and link previews:
 * bold markers stripped, whitespace collapsed, cut at a word boundary with an
 * ellipsis when longer than [maxLength].
 */
fun excerpt(body: String, maxLength: Int = 160): String {
    val text = stripBold(body).replace(WHITESPACE, " ").trim()
    if (text.length <= maxLength) return text
    val cut = text.take(maxLength - 1)
    val lastSpace = cut.lastIndexOf(' ')
    val trimmed = if (lastSpace > maxLength / 2) cut.take(lastSpace) else cut
    return trimmed.trimEnd(',', ';', ':', '.', ' ') + "…"
}

/**
 * Turns a kebab-case taxonomy id ("proof-of-work") into a display label
 * ("Proof Of Work") - identical to androidApp's `Category.displayName()`, so
 * both platforms label categories the same way.
 */
fun Category.displayName(): String =
    id.split('-').joinToString(" ") { word -> word.replaceFirstChar { it.uppercase() } }

/** Display label for a category id from the compiled index (ids are validated by the compiler). */
fun categoryLabel(id: String): String = Category.fromId(id)?.displayName() ?: id
