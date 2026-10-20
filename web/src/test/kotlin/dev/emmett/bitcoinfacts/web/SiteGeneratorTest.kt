package dev.emmett.bitcoinfacts.web

import dev.emmett.bitcoinfacts.compiler.CompileResult
import dev.emmett.bitcoinfacts.compiler.ContentCompiler
import dev.emmett.bitcoinfacts.compiler.FactOutput
import dev.emmett.bitcoinfacts.compiler.SourceOutput
import java.io.File
import java.nio.file.Files
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SiteGeneratorTest {

    private val config = SiteConfig(
        baseUrl = "https://example.test/bitcoin-bites/",
        appDownloadUrl = "https://example.test/download/bitcoin-bites.apk",
    )
    private val siteSourceDir = File(System.getProperty("siteSourceDir"))
    private val templatesDir = File(System.getProperty("templatesDir"))
    private val outputDir: File = Files.createTempDirectory("site").toFile()

    @AfterTest
    fun cleanUp() {
        outputDir.deleteRecursively()
    }

    private fun fact(id: String, order: Int, related: List<String> = emptyList(), body: String = "Body of $id.") =
        FactOutput(
            id = id,
            title = "Title $id",
            categories = listOf("money"),
            sources = listOf(SourceOutput("Book", "https://example.test/book")),
            notifyEligible = true,
            difficulty = "beginner",
            related = related,
            body = body,
            order = order,
        )

    private val fixtureFacts = listOf(
        fact("first", 1, related = listOf("second")),
        fact("second", 2, related = listOf("first"), body = "Uses **bold** & <tags>.\n\n**Analogy:** like a lock."),
        fact("lonely", 3),
    )

    private fun generate(facts: List<FactOutput> = fixtureFacts) =
        SiteGenerator(config, siteSourceDir, templatesDir).generate(facts, outputDir)

    private fun read(path: String) = File(outputDir, path).readText()

    @Test
    fun `every fact gets a page at its stable URL`() {
        generate()
        for (fact in fixtureFacts) {
            assertTrue(File(outputDir, "facts/${fact.id}/index.html").isFile, "missing page for ${fact.id}")
        }
    }

    @Test
    fun `fact page contains the formatted, escaped fact`() {
        generate()
        val page = read("facts/second/index.html")
        assertTrue(page.contains("Uses <strong>bold</strong> &amp; &lt;tags&gt;."))
        assertTrue(page.contains("<aside class=\"analogy\"><p><strong>Analogy:</strong> like a lock.</p></aside>"))
        assertFalse(page.contains("<tags>"))
    }

    @Test
    fun `related facts are linked, and omitted when there are none`() {
        generate()
        assertTrue(read("facts/first/index.html").contains("href=\"../../facts/second/\""))
        assertFalse(read("facts/lonely/index.html").contains("Related bites"))
    }

    @Test
    fun `internal links and assets are relative so the site works under a sub-path`() {
        generate()
        val linkAttr = Regex("(?:href|src)=\"([^\"]*)\"")
        val pages = outputDir.walkTopDown().filter { it.extension == "html" && it.name != "404.html" }.toList()
        assertTrue(pages.size > fixtureFacts.size)
        for (page in pages) {
            for (match in linkAttr.findAll(page.readText())) {
                val target = match.groupValues[1]
                assertFalse(target.startsWith("/"), "${page.relativeTo(outputDir)} links to root-absolute $target")
            }
        }
    }

    @Test
    fun `not-found page uses the base path since it is served at any depth`() {
        generate()
        val page = read("404.html")
        assertTrue(page.contains("href=\"/bitcoin-bites/styles.css\""))
        assertTrue(page.contains("href=\"/bitcoin-bites/\""))
    }

    @Test
    fun `link previews use absolute URLs from the base URL`() {
        generate()
        val page = read("facts/first/index.html")
        assertTrue(page.contains("<meta property=\"og:url\" content=\"https://example.test/bitcoin-bites/facts/first/\">"))
        assertTrue(page.contains("<meta property=\"og:title\" content=\"Title first\">"))
        assertTrue(page.contains("<meta property=\"og:description\" content=\"Body of first.\">"))
        assertTrue(page.contains("<link rel=\"canonical\" href=\"https://example.test/bitcoin-bites/facts/first/\">"))
    }

    @Test
    fun `get the app link uses the configured download URL`() {
        generate()
        val target = "href=\"https://example.test/download/bitcoin-bites.apk\""
        assertTrue(read("index.html").contains(target))
        assertTrue(read("facts/first/index.html").contains(target))
    }

    @Test
    fun `sitemap lists home, all bites and every fact`() {
        generate()
        val sitemap = read("sitemap.xml")
        assertTrue(sitemap.contains("<loc>https://example.test/bitcoin-bites/</loc>"))
        assertTrue(sitemap.contains("<loc>https://example.test/bitcoin-bites/facts/</loc>"))
        for (fact in fixtureFacts) {
            assertTrue(sitemap.contains("<loc>https://example.test/bitcoin-bites/facts/${fact.id}/</loc>"))
        }
    }

    @Test
    fun `service worker precaches every page and asset with a content-derived version`() {
        generate()
        val sw = read("sw.js")
        assertTrue(sw.contains("\"./\""))
        assertTrue(sw.contains("\"facts/second/\""))
        assertTrue(sw.contains("\"facts.json\""))
        assertTrue(sw.contains("\"styles.css\""))
        assertFalse(sw.contains("__PRECACHE_URLS__") || sw.contains("__CACHE_VERSION__"))

        val version = Regex("CACHE_PREFIX \\+ \"([0-9a-f]+)\"").find(sw)!!.groupValues[1]
        generate(fixtureFacts.map { if (it.id == "lonely") it.copy(title = "Changed") else it })
        val newVersion = Regex("CACHE_PREFIX \\+ \"([0-9a-f]+)\"").find(read("sw.js"))!!.groupValues[1]
        assertTrue(version != newVersion, "cache version should change when content changes")
    }

    @Test
    fun `facts json carries pre-rendered content for the journeys`() {
        generate()
        val json = read("facts.json")
        assertTrue(json.contains("\"path\":\"facts/second/\""))
        assertTrue(json.contains("&lt;tags&gt;"))
    }

    @Test
    fun `every real fact id produces a page - the URL the Android share footer builds`() {
        val result = ContentCompiler.compile(File(System.getProperty("contentFactsDir")))
        check(result is CompileResult.Success) { "real content failed to compile: $result" }
        generate(result.facts)
        for (fact in result.facts) {
            assertTrue(File(outputDir, "facts/${fact.id}/index.html").isFile, "missing page for ${fact.id}")
        }
    }

    @Test
    fun `invalid content fails the build and leaves no site behind`() {
        File(outputDir, "stale.html").writeText("old")
        val ok = buildSite(
            inputDir = File(System.getProperty("invalidFixturesDir")),
            outputDir = outputDir,
            siteSourceDir = siteSourceDir,
            templatesDir = templatesDir,
            config = config,
        )
        assertFalse(ok)
        assertFalse(outputDir.exists())
    }

    @Test
    fun `site files are written relative to the output directory`() {
        generate()
        assertEquals(
            setOf("index.html", "404.html", "facts.json", "sitemap.xml", "sw.js", "manifest.webmanifest", "styles.css"),
            outputDir.listFiles()!!.filter { it.isFile }.map { it.name }.toSet(),
        )
    }
}
