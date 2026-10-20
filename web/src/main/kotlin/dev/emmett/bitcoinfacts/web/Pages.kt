package dev.emmett.bitcoinfacts.web

import dev.emmett.bitcoinfacts.compiler.FactOutput

/**
 * Sources are kept on every fact (the compiler still requires them) but hidden,
 * matching androidApp's `SHOW_SOURCES` in FactContent.kt - flip both together
 * once we decide how citations should look.
 */
internal const val SHOW_SOURCES = false

const val SITE_NAME = "Bitcoin Bites"
const val TAGLINE = "Bitcoin Knowledge — one bite at a time"

/** Everything a page template needs that isn't the page's own content. */
data class SiteConfig(
    /** Absolute, with trailing slash, e.g. `https://walshe.github.io/bitcoin-bites/`. */
    val baseUrl: String,
    val appDownloadUrl: String,
) {
    /** Path part of [baseUrl] (`/bitcoin-bites/`) - only the 404 page uses it, see [notFoundPage]. */
    val basePath: String = java.net.URI(baseUrl).path.ifEmpty { "/" }

    /** A direct APK link (rather than a store listing) gets a short sideloading note. */
    val appIsDirectDownload: Boolean = appDownloadUrl.substringBefore('?').endsWith(".apk")
}

/**
 * One generated page. [path] is the site-relative directory URL ("" for home,
 * "facts/what-is-bitcoin/") - the page is written to `<path>index.html`. The
 * one exception is 404.html, whose [path] is already a file name.
 */
data class Page(val path: String, val html: String) {
    val file: String = if (path.endsWith(".html")) path else path + "index.html"
}

/** Relative prefix from a page at [path] back to the site root: "./" or "../../". */
fun rootPrefix(path: String): String {
    val depth = path.count { it == '/' }
    return if (depth == 0) "./" else "../".repeat(depth)
}

fun factPath(id: String) = "facts/$id/"

private data class Meta(
    val title: String,
    val description: String,
    val path: String,
    val ogType: String = "website",
)

private object Icons {
    private fun svg(body: String) =
        "<svg viewBox=\"0 0 24 24\" width=\"24\" height=\"24\" aria-hidden=\"true\" focusable=\"false\">$body</svg>"

    val heart = svg("<path class=\"heart\" d=\"M12 21s-7.5-4.6-10-9.3C.4 8.4 2.3 4 6.3 4c2.3 0 3.9 1.3 5.7 3.4C13.8 5.3 15.4 4 17.7 4c4 0 5.9 4.4 4.3 7.7C19.5 16.4 12 21 12 21z\"/>")
    val share = svg("<path d=\"M18 16a3 3 0 0 0-2.4 1.2l-6.8-3.4a3 3 0 0 0 0-1.6l6.8-3.4A3 3 0 1 0 15 7l-6.8 3.4a3 3 0 1 0 0 3.2L15 17a3 3 0 1 0 3-1z\"/>")
    val prev = svg("<path d=\"M15.4 7.4 14 6l-6 6 6 6 1.4-1.4L10.8 12z\"/>")
    val next = svg("<path d=\"M8.6 16.6 10 18l6-6-6-6-1.4 1.4 4.6 4.6z\"/>")
    val play = svg("<path d=\"M8 5v14l11-7z\"/>")
    val shuffle = svg("<path d=\"M17.6 6.4A8 8 0 1 0 19.7 14h-2.1a6 6 0 1 1-1.4-6.2L13 11h7V4z\"/>")
    val list = svg("<path d=\"M3 18h18v-2H3zm0-5h18v-2H3zm0-7v2h18V6z\"/>")
}

private fun favoriteButton(id: String) =
    """<button type="button" class="icon-button fav" data-fav="${html(id)}" aria-pressed="false" aria-label="Favorite">${Icons.heart}</button>"""

private fun shareButton(fact: FactOutput, config: SiteConfig) =
    """<button type="button" class="icon-button" data-share data-share-title="${html(fact.title)}" """ +
        """data-share-text="${html(shareText(fact))}" data-share-url="${html(config.baseUrl + factPath(fact.id))}" """ +
        """aria-label="Share">${Icons.share}</button>"""

/** Same body as the Android share text; the attribution and URL are appended by share.js. */
fun shareText(fact: FactOutput): String = "${fact.title}\n\n${stripBold(fact.body).trim()}"

/** Category chips plus the color-coded difficulty, as in androidApp's `CategoryAndDifficultyRow`. */
fun metaHtml(fact: FactOutput): String = buildString {
    append("<div class=\"meta\">")
    fact.categories.forEach { append("<span class=\"chip\">").append(html(categoryLabel(it))).append("</span>") }
    fact.difficulty?.let { difficulty ->
        val level = when (difficulty) {
            "beginner", "advanced" -> difficulty
            else -> "intermediate"
        }
        append("<span class=\"difficulty $level\">")
            .append(html(difficulty.replaceFirstChar { it.uppercase() }))
            .append("</span>")
    }
    append("</div>")
}

/** The fact's metadata and body - shared by the fact page and journey cards (via facts.json). */
fun factContentHtml(fact: FactOutput): String = metaHtml(fact) + "\n<div class=\"fact-body\">\n" + bodyToHtml(fact.body) + "</div>"

private fun layout(
    meta: Meta,
    pageType: String,
    config: SiteConfig,
    main: String,
    root: String = rootPrefix(meta.path),
): String {
    val fullTitle = if (meta.path.isEmpty()) "$SITE_NAME — $TAGLINE" else "${meta.title} · $SITE_NAME"
    val url = config.baseUrl + meta.path
    val image = config.baseUrl + "icons/og-image.png"
    return """<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover">
<title>${html(fullTitle)}</title>
<meta name="description" content="${html(meta.description)}">
<link rel="canonical" href="${html(url)}">
<meta property="og:type" content="${meta.ogType}">
<meta property="og:site_name" content="$SITE_NAME">
<meta property="og:title" content="${html(meta.title)}">
<meta property="og:description" content="${html(meta.description)}">
<meta property="og:url" content="${html(url)}">
<meta property="og:image" content="${html(image)}">
<meta property="og:image:width" content="1024">
<meta property="og:image:height" content="500">
<meta name="twitter:card" content="summary_large_image">
<meta name="twitter:title" content="${html(meta.title)}">
<meta name="twitter:description" content="${html(meta.description)}">
<meta name="twitter:image" content="${html(image)}">
<meta name="theme-color" content="#121212">
<link rel="manifest" href="${root}manifest.webmanifest">
<link rel="icon" type="image/png" sizes="32x32" href="${root}icons/favicon-32.png">
<link rel="apple-touch-icon" href="${root}icons/apple-touch-icon.png">
<link rel="stylesheet" href="${root}styles.css">
<script type="module" src="${root}js/main.js"></script>
</head>
<body data-page="$pageType" data-root="$root" data-site-url="${html(config.baseUrl)}">
<header class="topbar">
<a class="brand" href="$root"><img src="${root}icons/bitcoin-symbol.png" alt="" width="28" height="28">$SITE_NAME</a>
<nav class="topbar-links">
<a class="get-app-link" href="${html(config.appDownloadUrl)}">Get the app</a>
<a class="icon-button" href="${root}favorites/" aria-label="Favorites">${Icons.heart}</a>
</nav>
</header>
<main>
$main
</main>
<div class="toast" role="status" aria-live="polite" hidden></div>
</body>
</html>
"""
}

fun homePage(config: SiteConfig): Page {
    val root = rootPrefix("")
    val sideloadNote = if (config.appIsDirectDownload) {
        """<p class="note">Direct APK download while the app is in testing on Google Play. Android will ask you to allow installs from your browser.</p>"""
    } else {
        ""
    }
    val main = """<section class="hero">
<img class="mascot" src="${root}icons/mascot-400.webp" alt="" width="200" height="200">
<h1>$SITE_NAME</h1>
<p class="tagline">$TAGLINE</p>
<p class="streak" data-streak hidden></p>
</section>
<nav class="home-actions" aria-label="Start reading">
<a class="button primary" href="${root}guided/">${Icons.play}<span>Guided Bites</span></a>
<a class="button" href="${root}random/">${Icons.shuffle}<span>Random Bites</span></a>
<a class="button" href="${root}facts/">${Icons.list}<span>All Bites</span></a>
</nav>
<a class="continue-card" data-continue href="${root}guided/" hidden>
<span class="label">Continue reading</span>
<span class="title" data-continue-title></span>
</a>
<section class="get-app">
<a class="button" href="${html(config.appDownloadUrl)}">Get the Android app</a>
$sideloadNote
</section>"""
    val meta = Meta(
        title = SITE_NAME,
        description = "Short, plain-English Bitcoin explanations, each with an everyday analogy. Read them in order, at random, or browse them all.",
        path = "",
    )
    return Page("", layout(meta, "home", config, main))
}

fun factListPage(facts: List<FactOutput>, categoriesInUse: List<String>, config: SiteConfig): Page {
    val path = "facts/"
    val root = rootPrefix(path)
    val chips = categoriesInUse.joinToString("\n") {
        """<button type="button" class="chip-toggle" data-category="${html(it)}" aria-pressed="false">${html(categoryLabel(it))}</button>"""
    }
    val items = facts.joinToString("\n") { fact ->
        """<li data-categories="${html(fact.categories.joinToString(" "))}">
<a href="$root${factPath(fact.id)}"><span class="title">${html(fact.title)}</span><span class="cats">${html(fact.categories.joinToString(" · ") { categoryLabel(it) })}</span></a>
${favoriteButton(fact.id)}
</li>"""
    }
    val main = """<h1>All Bites</h1>
<p class="subtitle">${facts.size} bites</p>
<div class="filter" role="group" aria-label="Filter by category" data-filter hidden>
$chips
</div>
<p class="empty" data-filter-empty hidden>No bites match this selection yet.</p>
<ul class="fact-list">
$items
</ul>"""
    val meta = Meta(
        title = "All Bites",
        description = "Every Bitcoin Bites explanation, filterable by category.",
        path = path,
    )
    return Page(path, layout(meta, "list", config, main))
}

fun factPage(fact: FactOutput, factsById: Map<String, FactOutput>, config: SiteConfig): Page {
    val path = factPath(fact.id)
    val root = rootPrefix(path)
    val related = fact.related.mapNotNull { factsById[it] }
    val relatedHtml = if (related.isEmpty()) {
        ""
    } else {
        "<section class=\"related\">\n<h2>Related bites</h2>\n<ul class=\"fact-list\">\n" +
            related.joinToString("\n") { """<li><a href="$root${factPath(it.id)}"><span class="title">${html(it.title)}</span></a></li>""" } +
            "\n</ul>\n</section>"
    }
    val sourcesHtml = if (!SHOW_SOURCES) {
        ""
    } else {
        "<section class=\"sources\">\n<h2>Sources</h2>\n<ul>\n" +
            fact.sources.joinToString("\n") { """<li><a href="${html(it.url)}" rel="noopener">${html(it.title)}</a></li>""" } +
            "\n</ul>\n</section>"
    }
    val main = """<article class="fact" data-fact-id="${html(fact.id)}">
<a class="back" href="${root}facts/">All Bites</a>
<h1>${html(fact.title)}</h1>
${factContentHtml(fact)}
<div class="actions">
${favoriteButton(fact.id)}
${shareButton(fact, config)}
</div>
$sourcesHtml
$relatedHtml
</article>"""
    val meta = Meta(title = fact.title, description = excerpt(fact.body), path = path, ogType = "article")
    return Page(path, layout(meta, "fact", config, main))
}

/** [mode] is "guided" or "random"; the journey itself is built client-side by journey.js. */
fun journeyPage(mode: String, config: SiteConfig): Page {
    val path = "$mode/"
    val root = rootPrefix(path)
    val title = if (mode == "guided") "Guided Bites" else "Random Bites"
    val main = """<section class="journey" data-journey="$mode">
<div class="journey-bar">
<h1>$title</h1>
<span class="position" data-journey-position></span>
</div>
<div class="journey-card" data-journey-card aria-live="polite"><p class="loading">Loading…</p></div>
<div class="journey-controls" data-journey-controls hidden>
<button type="button" class="icon-button" data-journey-prev aria-label="Previous bite">${Icons.prev}</button>
<button type="button" class="icon-button fav" data-journey-fav aria-pressed="false" aria-label="Favorite">${Icons.heart}</button>
<button type="button" class="icon-button" data-journey-share aria-label="Share">${Icons.share}</button>
<button type="button" class="icon-button" data-journey-next aria-label="Next bite">${Icons.next}</button>
</div>
<noscript><p class="empty">Journeys need JavaScript. <a href="${root}facts/">Browse all bites</a> instead.</p></noscript>
</section>"""
    val description = if (mode == "guided") {
        "Read every Bitcoin Bites explanation in order, one bite at a time."
    } else {
        "Shuffle through every Bitcoin Bites explanation, one bite at a time."
    }
    return Page(path, layout(Meta(title, description, path), "journey", config, main))
}

fun favoritesPage(config: SiteConfig): Page {
    val path = "favorites/"
    val root = rootPrefix(path)
    val main = """<h1>Favorites</h1>
<ul class="fact-list" data-favorites-list></ul>
<p class="empty" data-favorites-empty hidden>No favorites yet - tap the heart on any fact to save it here.</p>
<noscript><p class="empty">Favorites need JavaScript. <a href="${root}facts/">Browse all bites</a> instead.</p></noscript>"""
    return Page(path, layout(Meta("Favorites", "Your saved Bitcoin Bites.", path), "favorites", config, main))
}

/**
 * GitHub Pages serves 404.html for a missing path at any depth, so relative
 * links can't work here - this one page uses root-absolute links built from
 * the base URL's path instead (design.md decision 8).
 */
fun notFoundPage(config: SiteConfig): Page {
    val main = """<section class="empty-state">
<h1>Bite not found</h1>
<p>That page doesn't exist - it may have moved.</p>
<a class="button primary" href="${config.basePath}">Back to the home page</a>
</section>"""
    val meta = Meta("Not found", "This page doesn't exist.", "404.html")
    return Page("404.html", layout(meta, "notfound", config, main, root = config.basePath))
}
