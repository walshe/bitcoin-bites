package dev.emmett.bitcoinfacts.web

import dev.emmett.bitcoinfacts.compiler.FactOutput
import dev.emmett.bitcoinfacts.taxonomy.Category
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.security.MessageDigest

/**
 * Shape of the site's `facts.json`: the compiled fact plus what the client-side
 * journeys need pre-rendered, so the browser never formats or escapes fact
 * content itself (design.md decision 2).
 */
@Serializable
data class WebFact(
    val id: String,
    val title: String,
    val order: Int,
    val categories: List<String>,
    val difficulty: String?,
    val related: List<String>,
    /** Site-relative URL of the fact's own page. */
    val path: String,
    /** Metadata + body markup, already escaped - see [factContentHtml]. */
    val html: String,
    /** Plain-text share body, as on Android. */
    val shareText: String,
)

@Serializable
data class WebFactIndex(val schemaVersion: Int = 1, val facts: List<WebFact>)

private val siteJson = Json { encodeDefaults = true }

/**
 * Writes the whole static site for already-compiled, already-validated [facts]
 * into [outputDir]: pages, facts.json, sitemap.xml, the static assets from
 * [siteSourceDir], and a service worker generated from [templatesDir]/sw.js.
 */
class SiteGenerator(
    private val config: SiteConfig,
    private val siteSourceDir: File,
    private val templatesDir: File,
) {

    fun generate(facts: List<FactOutput>, outputDir: File) {
        outputDir.deleteRecursively()
        outputDir.mkdirs()

        siteSourceDir.copyRecursively(outputDir, overwrite = true)

        val pages = pages(facts)
        for (page in pages) {
            File(outputDir, page.file).apply { parentFile.mkdirs() }.writeText(page.html)
        }

        val index = WebFactIndex(facts = facts.map { it.toWebFact() })
        File(outputDir, "facts.json").writeText(siteJson.encodeToString(index))
        File(outputDir, "sitemap.xml").writeText(sitemap(pages))

        writeServiceWorker(outputDir)
    }

    fun pages(facts: List<FactOutput>): List<Page> {
        val factsById = facts.associateBy { it.id }
        val usedCategories = facts.flatMap { it.categories }.toSet()
        // Taxonomy order, not alphabetical - the same order the enum (and the app) uses.
        val categoriesInUse = Category.entries.map { it.id }.filter { it in usedCategories }
        return buildList {
            add(homePage(config))
            add(factListPage(facts, categoriesInUse, config))
            facts.forEach { add(factPage(it, factsById, config)) }
            add(journeyPage("guided", config))
            add(journeyPage("random", config))
            add(favoritesPage(config))
            add(notFoundPage(config))
        }
    }

    private fun FactOutput.toWebFact() = WebFact(
        id = id,
        title = title,
        order = order,
        categories = categories,
        difficulty = difficulty,
        related = related,
        path = factPath(id),
        html = factContentHtml(this),
        shareText = shareText(this),
    )

    /** Every page except the 404 and the per-user shells, which have nothing to index. */
    private fun sitemap(pages: List<Page>): String {
        val indexed = pages.filter { it.path == "" || it.path.startsWith("facts/") }
        return buildString {
            append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
            append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n")
            indexed.forEach { append("  <url><loc>").append(html(config.baseUrl + it.path)).append("</loc></url>\n") }
            append("</urlset>\n")
        }
    }

    /**
     * Precaches every file in the site (design.md decision 9). Directory pages
     * are listed by their directory URL ("facts/x/"), since that's what
     * navigations request. The cache name is a hash of the site's contents, so
     * any change to any file produces a new cache and evicts the old one.
     */
    private fun writeServiceWorker(outputDir: File) {
        val files = outputDir.walkTopDown()
            .filter { it.isFile }
            .map { it.relativeTo(outputDir).invariantSeparatorsPath }
            .filter { it != "sw.js" }
            .sorted()
            .toList()

        val digest = MessageDigest.getInstance("SHA-256")
        files.forEach { path ->
            digest.update(path.toByteArray())
            digest.update(File(outputDir, path).readBytes())
        }
        val version = digest.digest().take(6).joinToString("") { "%02x".format(it) }

        val urls = files.map { path ->
            when {
                path == "index.html" -> "./"
                path.endsWith("/index.html") -> path.removeSuffix("index.html")
                else -> path
            }
        }
        val template = File(templatesDir, "sw.js").readText()
        val sw = template
            .replace("__CACHE_VERSION__", version)
            .replace("__PRECACHE_URLS__", urls.joinToString(",\n  ") { "\"$it\"" })
        File(outputDir, "sw.js").writeText(sw)
    }
}
