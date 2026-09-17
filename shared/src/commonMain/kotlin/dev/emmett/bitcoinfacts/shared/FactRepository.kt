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

    /** Empty [selectedCategories] means no filter applied - every fact is returned. */
    fun filterByCategories(selectedCategories: Set<Category>): List<Fact> {
        if (selectedCategories.isEmpty()) return facts
        return facts.filter { fact -> fact.categories.any { it in selectedCategories } }
    }

    /** Resolves a fact's `related` IDs to their actual [Fact] objects, dropping any that can't be found. */
    fun relatedFacts(fact: Fact): List<Fact> = fact.related.mapNotNull { findById(it) }
}
