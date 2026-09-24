package dev.emmett.bitcoinfacts.shared

import dev.emmett.bitcoinfacts.shared.model.Fact
import dev.emmett.bitcoinfacts.taxonomy.Category

/**
 * In-memory view over the loaded fact set: browsing, category filtering (union
 * semantics across selected categories, per the fact-discovery spec), and
 * related-facts resolution. Facts are loaded once at startup ([FactIndexLoader])
 * and held here for the lifetime of the app - there is no runtime mutation.
 */
class FactRepository(private val facts: List<Fact>) {

    fun all(): List<Fact> = facts

    fun findById(id: String): Fact? = facts.find { it.id == id }

    /**
     * Text facts only (Browse All / Journey) - a fact with a [Fact.videoUrl] is
     * excluded here and surfaced instead via [videoFacts], since the swipeable
     * journey and the plain list row aren't built to render a video embed.
     */
    private val textFacts: List<Fact> = facts.filter { it.videoUrl == null }

    /** Empty [selectedCategories] means no filter applied - every text fact is returned. */
    fun filterByCategories(selectedCategories: Set<Category>): List<Fact> {
        if (selectedCategories.isEmpty()) return textFacts
        return textFacts.filter { fact -> fact.categories.any { it in selectedCategories } }
    }

    /** Facts with a [Fact.videoUrl] set, for the All Videos screen. */
    fun videoFacts(): List<Fact> = facts.filter { it.videoUrl != null }

    /** Resolves a fact's `related` IDs to their actual [Fact] objects, dropping any that can't be found. */
    fun relatedFacts(fact: Fact): List<Fact> = fact.related.mapNotNull { findById(it) }
}
