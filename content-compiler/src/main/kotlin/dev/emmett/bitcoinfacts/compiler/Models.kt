package dev.emmett.bitcoinfacts.compiler

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

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
    val order: Int,
    val videoUrl: String? = null,
)

@Serializable
data class SourceOutput(
    val title: String,
    val url: String,
)

/**
 * Top-level envelope written to facts.json. [schemaVersion] exists purely as a
 * forward-compatibility hook (design.md decision 11): v1 has no remote refresh
 * mechanism and content is frozen at build time, but IF a future change adds one,
 * the app can check this field to detect an index format it doesn't understand
 * before attempting to parse it, without any rework of the compiler itself.
 * Bump this only when [FactOutput]'s shape changes in a way older app versions
 * couldn't parse.
 */
@Serializable
data class FactIndexOutput(
    val schemaVersion: Int = CURRENT_SCHEMA_VERSION,
    val facts: List<FactOutput>,
) {
    companion object {
        const val CURRENT_SCHEMA_VERSION = 1
    }
}

/**
 * Shared JSON config for writing the compiled index. `encodeDefaults = true` is
 * essential here: without it, [FactIndexOutput.schemaVersion] would be silently
 * omitted from the output whenever it equals its default, defeating the whole
 * point of the field (see design.md decision 11).
 */
val outputJson = Json {
    prettyPrint = true
    encodeDefaults = true
}
