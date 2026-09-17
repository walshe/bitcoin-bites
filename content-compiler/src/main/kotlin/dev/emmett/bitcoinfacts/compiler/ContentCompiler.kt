package dev.emmett.bitcoinfacts.compiler

import dev.emmett.bitcoinfacts.taxonomy.Category
import java.io.File

sealed interface CompileResult {
    data class Success(val facts: List<FactOutput>) : CompileResult
    data class Failure(val errors: List<String>) : CompileResult
}

/**
 * Parses and validates every `*.md` fact in [inputDir], mirrors symmetric `related`
 * links, and returns either the compiled facts or every validation error found
 * (collected together, not just the first, so a build failure names every
 * problem in one pass).
 */
object ContentCompiler {

    fun compile(inputDir: File): CompileResult {
        val files = inputDir.listFiles { f ->
            f.isFile && f.extension == "md" && !f.name.equals("README.md", ignoreCase = true)
        }
            ?.sortedBy { it.name }
            ?: emptyList()

        val errors = mutableListOf<String>()
        val seenIds = mutableMapOf<String, File>()
        val parsedFacts = mutableListOf<FactOutput>()

        for (file in files) {
            val parsed = try {
                FrontmatterParser.parse(file)
            } catch (e: FrontmatterParseException) {
                errors += e.message ?: "${file.name}: frontmatter parse error"
                continue
            }
            val fm = parsed.frontmatter

            val id = fm["id"] as? String
            if (id.isNullOrBlank()) {
                errors += "${file.name}: missing required field 'id'"
                continue
            }

            val expectedId = file.nameWithoutExtension
            if (id != expectedId) {
                errors += "${file.name}: id '$id' does not match filename (expected '$expectedId.md')"
            }

            val previousFile = seenIds[id]
            if (previousFile != null) {
                errors += "${file.name}: duplicate id '$id' (already declared in ${previousFile.name})"
            } else {
                seenIds[id] = file
            }

            val title = fm["title"] as? String
            if (title.isNullOrBlank()) {
                errors += "${file.name}: missing required field 'title'"
            }

            @Suppress("UNCHECKED_CAST")
            val categories = fm["categories"] as? List<String>
            if (categories.isNullOrEmpty()) {
                errors += "${file.name}: missing required non-empty field 'categories'"
            } else {
                for (category in categories) {
                    if (Category.fromId(category) == null) {
                        errors += "${file.name}: unknown category '$category' " +
                            "(canonical taxonomy: ${Category.allIds.sorted().joinToString()})"
                    }
                }
            }

            @Suppress("UNCHECKED_CAST")
            val sourcesRaw = fm["sources"] as? List<Map<String, Any?>>
            val sources = mutableListOf<SourceOutput>()
            if (sourcesRaw.isNullOrEmpty()) {
                errors += "${file.name}: missing required non-empty field 'sources'"
            } else {
                sourcesRaw.forEachIndexed { index, source ->
                    val sourceTitle = source["title"] as? String
                    val sourceUrl = source["url"] as? String
                    if (sourceTitle.isNullOrBlank() || sourceUrl.isNullOrBlank()) {
                        errors += "${file.name}: sources[$index] must have non-empty 'title' and 'url'"
                    } else {
                        sources += SourceOutput(sourceTitle, sourceUrl)
                    }
                }
            }

            val notifyEligible = fm["notify_eligible"]
            if (notifyEligible !is Boolean) {
                errors += "${file.name}: missing/invalid required boolean field 'notify_eligible'"
            }

            val difficulty = fm["difficulty"] as? String

            @Suppress("UNCHECKED_CAST")
            val related = (fm["related"] as? List<String>) ?: emptyList()

            val order = fm["order"] as? Int
            if (order == null) {
                errors += "${file.name}: missing/invalid required integer field 'order'"
            }

            val hasFatalFieldError = title.isNullOrBlank() ||
                categories.isNullOrEmpty() ||
                sourcesRaw.isNullOrEmpty() ||
                notifyEligible !is Boolean ||
                order == null
            if (hasFatalFieldError) continue

            parsedFacts += FactOutput(
                id = id,
                title = title!!,
                categories = categories!!,
                sources = sources,
                notifyEligible = notifyEligible as Boolean,
                difficulty = difficulty,
                related = related,
                body = parsed.body,
                order = order,
            )
        }

        // Dangling-related-id check always runs (not short-circuited by earlier field
        // errors) so a single compile() call surfaces every problem at once rather than
        // hiding later error classes behind earlier ones.
        val allIds = parsedFacts.map { it.id }.toSet()
        for (fact in parsedFacts) {
            for (relatedId in fact.related) {
                if (relatedId !in allIds) {
                    errors += "${fact.id}: related fact '$relatedId' does not exist"
                }
            }
        }

        if (errors.isNotEmpty()) return CompileResult.Failure(errors)

        return CompileResult.Success(mirrorRelated(parsedFacts))
    }

    /**
     * Pure mirroring step, extracted so it's unit-testable on plain [FactOutput]
     * objects without going through file parsing or category validation (see
     * ContentCompilerTest - this used to be tested only via real content/facts
     * files, which broke the test every time the real library's fact IDs changed).
     */
    internal fun mirrorRelated(facts: List<FactOutput>): List<FactOutput> {
        val mirroredRelated = facts.associate { it.id to it.related.toMutableSet() }
        for (fact in facts) {
            for (relatedId in fact.related) {
                mirroredRelated.getValue(relatedId).add(fact.id)
            }
        }
        return facts
            .map { fact -> fact.copy(related = mirroredRelated.getValue(fact.id).toList().sorted()) }
            .sortedWith(compareBy({ it.order }, { it.id }))
    }
}
