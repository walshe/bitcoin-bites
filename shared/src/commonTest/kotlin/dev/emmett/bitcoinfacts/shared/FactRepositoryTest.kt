package dev.emmett.bitcoinfacts.shared

import dev.emmett.bitcoinfacts.shared.model.Fact
import dev.emmett.bitcoinfacts.shared.model.Source
import dev.emmett.bitcoinfacts.taxonomy.Category
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private fun fact(id: String, categories: List<Category>, related: List<String> = emptyList()) = Fact(
    id = id,
    title = "Title $id",
    categories = categories,
    sources = listOf(Source("src", "https://example.com")),
    notifyEligible = true,
    difficulty = null,
    related = related,
    body = "body",
)

class FactRepositoryTest {

    private val repo = FactRepository(
        listOf(
            fact("fact-mining", listOf(Category.MINING), related = listOf("fact-pow")),
            fact("fact-pow", listOf(Category.PROOF_OF_WORK), related = listOf("fact-mining")),
            fact("fact-keys-custody", listOf(Category.KEYS, Category.CUSTODY)),
            fact("fact-history", listOf(Category.HISTORY)),
        )
    )

    @Test
    fun `all returns every fact`() {
        assertEquals(4, repo.all().size)
    }

    @Test
    fun `empty category filter returns everything`() {
        assertEquals(4, repo.filterByCategories(emptySet()).size)
    }

    @Test
    fun `single category filter narrows the list`() {
        val result = repo.filterByCategories(setOf(Category.MINING))
        assertEquals(listOf("fact-mining"), result.map { it.id })
    }

    @Test
    fun `multi category filter is a union, not an intersection`() {
        val result = repo.filterByCategories(setOf(Category.KEYS, Category.HISTORY))
        assertEquals(setOf("fact-keys-custody", "fact-history"), result.map { it.id }.toSet())
    }

    @Test
    fun `related facts resolve to real fact objects`() {
        val miningFact = repo.findById("fact-mining")!!
        val related = repo.relatedFacts(miningFact)
        assertEquals(listOf("fact-pow"), related.map { it.id })
    }

    @Test
    fun `related facts is empty when a fact has no related ids`() {
        val historyFact = repo.findById("fact-history")!!
        assertTrue(repo.relatedFacts(historyFact).isEmpty())
    }
}
