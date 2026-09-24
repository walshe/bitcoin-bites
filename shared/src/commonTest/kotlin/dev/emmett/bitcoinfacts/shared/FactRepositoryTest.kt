package dev.emmett.bitcoinfacts.shared

import dev.emmett.bitcoinfacts.shared.model.Fact
import dev.emmett.bitcoinfacts.shared.model.Source
import dev.emmett.bitcoinfacts.taxonomy.Category
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private fun fact(
    id: String,
    categories: List<Category>,
    related: List<String> = emptyList(),
    order: Int = 0,
    videoUrl: String? = null,
) = Fact(
    id = id,
    title = "Title $id",
    categories = categories,
    sources = listOf(Source("src", "https://example.com")),
    notifyEligible = true,
    difficulty = null,
    related = related,
    body = "body",
    order = order,
    videoUrl = videoUrl,
)

class FactRepositoryTest {

    private val repo = FactRepository(
        listOf(
            fact("fact-fiat", listOf(Category.FIAT), related = listOf("fact-monetary-policy")),
            fact("fact-monetary-policy", listOf(Category.MONETARY_POLICY), related = listOf("fact-fiat")),
            fact("fact-privacy-custody", listOf(Category.PRIVACY, Category.CUSTODY)),
            fact("fact-history", listOf(Category.HISTORY)),
            fact("fact-video", listOf(Category.HISTORY), videoUrl = "https://x.com/example/status/1"),
        )
    )

    @Test
    fun `all returns every fact, including video facts`() {
        assertEquals(5, repo.all().size)
    }

    @Test
    fun `empty category filter returns every text fact, excluding video facts`() {
        val result = repo.filterByCategories(emptySet())
        assertEquals(4, result.size)
        assertTrue(result.none { it.id == "fact-video" })
    }

    @Test
    fun `category filter excludes video facts even when the category matches`() {
        val result = repo.filterByCategories(setOf(Category.HISTORY))
        assertEquals(listOf("fact-history"), result.map { it.id })
    }

    @Test
    fun `videoFacts returns only facts with a videoUrl`() {
        assertEquals(listOf("fact-video"), repo.videoFacts().map { it.id })
    }

    @Test
    fun `single category filter narrows the list`() {
        val result = repo.filterByCategories(setOf(Category.FIAT))
        assertEquals(listOf("fact-fiat"), result.map { it.id })
    }

    @Test
    fun `multi category filter is a union, not an intersection`() {
        val result = repo.filterByCategories(setOf(Category.PRIVACY, Category.HISTORY))
        assertEquals(setOf("fact-privacy-custody", "fact-history"), result.map { it.id }.toSet())
    }

    @Test
    fun `related facts resolve to real fact objects`() {
        val fiatFact = repo.findById("fact-fiat")!!
        val related = repo.relatedFacts(fiatFact)
        assertEquals(listOf("fact-monetary-policy"), related.map { it.id })
    }

    @Test
    fun `related facts is empty when a fact has no related ids`() {
        val historyFact = repo.findById("fact-history")!!
        assertTrue(repo.relatedFacts(historyFact).isEmpty())
    }
}
