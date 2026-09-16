package dev.emmett.bitcoinfacts.compiler

import org.yaml.snakeyaml.Yaml
import java.io.File

/** Raw result of splitting a fact file into its YAML frontmatter and Markdown body. */
data class ParsedFile(
    val frontmatter: Map<String, Any?>,
    val body: String,
)

class FrontmatterParseException(message: String) : Exception(message)

object FrontmatterParser {
    private val yaml = Yaml()

    fun parse(file: File): ParsedFile {
        val text = file.readText()
        val lines = text.lines()

        if (lines.firstOrNull()?.trim() != "---") {
            throw FrontmatterParseException("${file.name}: must start with a '---' frontmatter delimiter")
        }

        val closingIndex = lines.drop(1).indexOfFirst { it.trim() == "---" } + 1
        if (closingIndex <= 0) {
            throw FrontmatterParseException("${file.name}: frontmatter is missing its closing '---' delimiter")
        }

        val yamlText = lines.subList(1, closingIndex).joinToString("\n")
        val body = lines.subList(closingIndex + 1, lines.size).joinToString("\n").trim()

        @Suppress("UNCHECKED_CAST")
        val frontmatter = (yaml.load<Map<String, Any?>>(yamlText) ?: emptyMap()) as Map<String, Any?>

        return ParsedFile(frontmatter, body)
    }
}
