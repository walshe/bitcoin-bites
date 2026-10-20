package dev.emmett.bitcoinfacts.web

import dev.emmett.bitcoinfacts.taxonomy.Category
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FormattingTest {

    @Test
    fun `html escapes every special character`() {
        assertEquals("&lt;a href=&quot;x&quot;&gt;Tom &amp; Jerry&#39;s&lt;/a&gt;", html("<a href=\"x\">Tom & Jerry's</a>"))
    }

    @Test
    fun `bold markers become strong tags`() {
        assertEquals("A <strong>hash</strong> is <strong>fixed</strong>.", boldToHtml("A **hash** is **fixed**."))
    }

    @Test
    fun `text inside bold is still escaped`() {
        assertEquals("<strong>a &lt; b</strong>", boldToHtml("**a < b**"))
    }

    @Test
    fun `body without an analogy keeps every paragraph`() {
        val formatted = splitBody("One.\n\nTwo.")
        assertEquals(listOf("One.", "Two."), formatted.paragraphs)
        assertNull(formatted.analogy)
    }

    @Test
    fun `analogy paragraph is pulled out wherever it appears`() {
        val formatted = splitBody("One.\n\n**Analogy:** like a lock.\n\nThree.")
        assertEquals(listOf("One.", "Three."), formatted.paragraphs)
        assertEquals("**Analogy:** like a lock.", formatted.analogy)
    }

    @Test
    fun `body html wraps the analogy in an aside`() {
        val rendered = bodyToHtml("Main **text**.\n\n**Analogy:** a < b")
        assertEquals(
            "<p>Main <strong>text</strong>.</p>\n" +
                "<aside class=\"analogy\"><p><strong>Analogy:</strong> a &lt; b</p></aside>\n",
            rendered,
        )
    }

    @Test
    fun `body html contains no unintended elements`() {
        val rendered = bodyToHtml("a < b && c > d <script>alert(1)</script>")
        assertFalse(rendered.contains("<script>"))
        assertTrue(rendered.contains("a &lt; b &amp;&amp; c &gt; d"))
    }

    @Test
    fun `excerpt strips bold and collapses whitespace`() {
        assertEquals("A hash is fixed. And short.", excerpt("A **hash** is fixed.\n\nAnd   short."))
    }

    @Test
    fun `long excerpt is cut at a word boundary with an ellipsis`() {
        val text = "word ".repeat(60)
        val result = excerpt(text, maxLength = 40)
        assertTrue(result.length <= 40, "was ${result.length}: $result")
        assertTrue(result.endsWith("word…"), result)
    }

    @Test
    fun `every category has a display label`() {
        for (category in Category.entries) {
            assertTrue(category.displayName().isNotBlank(), "no label for ${category.id}")
        }
        assertEquals("Proof Of Work", Category.PROOF_OF_WORK.displayName())
        assertEquals("Money", categoryLabel("money"))
    }
}
