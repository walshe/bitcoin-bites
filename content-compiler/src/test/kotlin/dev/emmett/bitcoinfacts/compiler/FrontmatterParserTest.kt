package dev.emmett.bitcoinfacts.compiler

import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals

class FrontmatterParserTest {

    // Built with explicit "\n" joins (not a trimIndent template) so a multi-line
    // `body` can't have its own line breaks mistaken for template indentation.
    private fun fileWithBody(body: String) = Files.createTempFile("frontmatter-parser-test", ".md").toFile().apply {
        writeText(listOf("---", "id: fixture", "---", "", body).joinToString("\n"))
    }

    @Test
    fun `hard-wrapped lines within a paragraph reflow into a single line`() {
        val file = fileWithBody("Money kept with a bank can be frozen or seized — not\njust in theory.")
        val parsed = FrontmatterParser.parse(file)
        assertEquals("Money kept with a bank can be frozen or seized — not just in theory.", parsed.body)
    }

    @Test
    fun `a blank line still starts a new paragraph`() {
        val file = fileWithBody("First paragraph,\nwrapped across two lines.\n\nSecond paragraph.")
        val parsed = FrontmatterParser.parse(file)
        assertEquals("First paragraph, wrapped across two lines.\n\nSecond paragraph.", parsed.body)
    }
}
