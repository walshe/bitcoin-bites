package dev.emmett.bitcoinfacts.shared

import dev.emmett.bitcoinfacts.taxonomy.Category
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val SAMPLE_JSON = """
{
  "schemaVersion": 1,
  "facts": [
    {
      "id": "fact-001",
      "title": "Why 21 million?",
      "categories": ["monetary-policy", "scarcity"],
      "sources": [{"title": "Whitepaper", "url": "https://bitcoin.org/bitcoin.pdf"}],
      "notifyEligible": true,
      "difficulty": "beginner",
      "related": ["fact-002"],
      "body": "Fixed supply."
    },
    {
      "id": "fact-002",
      "title": "Your keys, your coins",
      "categories": ["privacy", "custody"],
      "sources": [{"title": "Mastering Bitcoin", "url": "https://example.com"}],
      "notifyEligible": true,
      "difficulty": null,
      "related": ["fact-001"],
      "body": "Self custody."
    }
  ]
}
""".trimIndent()

class FactIndexLoaderTest {

    @Test
    fun `parses facts with resolved categories`() {
        val facts = FactIndexLoader.parse(SAMPLE_JSON)
        assertEquals(2, facts.size)
        val fact001 = facts.first { it.id == "fact-001" }
        assertContains(fact001.categories, Category.MONETARY_POLICY)
        assertContains(fact001.categories, Category.SCARCITY)
        assertEquals("beginner", fact001.difficulty)
    }

    @Test
    fun `parses a null optional difficulty`() {
        val facts = FactIndexLoader.parse(SAMPLE_JSON)
        val fact002 = facts.first { it.id == "fact-002" }
        assertEquals(null, fact002.difficulty)
    }

    @Test
    fun `rejects an unsupported schema version`() {
        val badJson = SAMPLE_JSON.replaceFirst("\"schemaVersion\": 1", "\"schemaVersion\": 999")
        assertFailsWith<UnsupportedFactIndexSchemaException> {
            FactIndexLoader.parse(badJson)
        }
    }
}
