package dev.emmett.bitcoinfacts.shared.model

import dev.emmett.bitcoinfacts.taxonomy.Category

/**
 * In-memory domain model for a compiled fact. [related] stays as plain fact IDs
 * (not resolved [Fact] references) so facts don't need to hold cyclic references
 * to each other — resolving an ID to its [Fact] is [dev.emmett.bitcoinfacts.shared.FactRepository.relatedFacts]'s job (task 3.4).
 */
data class Fact(
    val id: String,
    val title: String,
    val categories: List<Category>,
    val sources: List<Source>,
    val notifyEligible: Boolean,
    val difficulty: String?,
    val related: List<String>,
    val body: String,
)

data class Source(
    val title: String,
    val url: String,
)
