package dev.emmett.bitcoinfacts.shared

import dev.emmett.bitcoinfacts.shared.model.Fact
import dev.emmett.bitcoinfacts.shared.model.Source
import dev.emmett.bitcoinfacts.taxonomy.Category
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class UnsupportedFactIndexSchemaException(found: Int, supported: Int) :
    Exception("facts.json schemaVersion $found is not supported by this app build (supports $supported)")

/**
 * Parses the bundled facts.json (produced by content-compiler) into domain [Fact]
 * objects. Deliberately takes raw JSON text rather than reading it itself — the
 * platform layer (e.g. androidApp reading its own assets) owns actual file I/O,
 * keeping this parsing logic pure and testable on any target without a Context.
 */
object FactIndexLoader {
    /** Must match content-compiler's FactIndexOutput.CURRENT_SCHEMA_VERSION. */
    const val SUPPORTED_SCHEMA_VERSION = 1

    private val json = Json { ignoreUnknownKeys = true }

    fun parse(rawJson: String): List<Fact> {
        val index = json.decodeFromString<FactIndexDto>(rawJson)
        if (index.schemaVersion != SUPPORTED_SCHEMA_VERSION) {
            throw UnsupportedFactIndexSchemaException(index.schemaVersion, SUPPORTED_SCHEMA_VERSION)
        }
        return index.facts.map { it.toDomain() }
    }

    private fun FactDto.toDomain(): Fact = Fact(
        id = id,
        title = title,
        categories = categories.map { categoryId ->
            Category.fromId(categoryId)
                ?: error("Unknown category '$categoryId' in fact '$id' - content compiler should have rejected this at build time")
        },
        sources = sources.map { Source(it.title, it.url) },
        notifyEligible = notifyEligible,
        difficulty = difficulty,
        related = related,
        body = body,
        order = order,
        videoUrl = videoUrl,
    )
}
