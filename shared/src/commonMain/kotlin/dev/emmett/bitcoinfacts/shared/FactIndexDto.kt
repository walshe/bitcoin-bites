package dev.emmett.bitcoinfacts.shared

import kotlinx.serialization.Serializable

/**
 * Wire shape of the bundled facts.json, matching content-compiler's
 * FactIndexOutput/FactOutput/SourceOutput exactly (field-for-field, same JSON
 * property names) so kotlinx.serialization can deserialize it directly.
 * Kept as a private/internal parsing detail — callers use [dev.emmett.bitcoinfacts.shared.model.Fact], not this.
 */
@Serializable
internal data class FactIndexDto(
    val schemaVersion: Int,
    val facts: List<FactDto>,
)

@Serializable
internal data class FactDto(
    val id: String,
    val title: String,
    val categories: List<String>,
    val sources: List<SourceDto>,
    val notifyEligible: Boolean,
    val difficulty: String? = null,
    val related: List<String> = emptyList(),
    val body: String,
    val order: Int,
    val videoUrl: String? = null,
)

@Serializable
internal data class SourceDto(
    val title: String,
    val url: String,
)
