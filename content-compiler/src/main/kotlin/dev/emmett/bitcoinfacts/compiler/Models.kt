package dev.emmett.bitcoinfacts.compiler

import kotlinx.serialization.Serializable

/**
 * Shape of the compiled runtime index bundled into the app. Kept intentionally
 * simple/flat so the shared Kotlin module (task group 3) can deserialize it
 * with kotlinx.serialization without any compiler-specific types.
 */
@Serializable
data class FactOutput(
    val id: String,
    val title: String,
    val categories: List<String>,
    val sources: List<SourceOutput>,
    val notifyEligible: Boolean,
    val difficulty: String?,
    val related: List<String>,
    val body: String,
)

@Serializable
data class SourceOutput(
    val title: String,
    val url: String,
)
