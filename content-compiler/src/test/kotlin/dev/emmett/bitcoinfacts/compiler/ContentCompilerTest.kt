package dev.emmett.bitcoinfacts.compiler

import kotlinx.serialization.encodeToString
import java.io.File
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ContentCompilerTest {

    // Real content/facts is the user's own evolving library (see design.md
    // decision 14: starts empty, grows organically) - these tests must not
    // assume anything about its current size or specific fact IDs.
    private val realFactsDir = File(System.getProperty("contentFactsDir"))
    private val invalidFixturesDir = File(System.getProperty("invalidFixturesDir"))

    private fun tempDirWith(vararg extraFiles: File): File {
        val dir = Files.createTempDirectory("content-compiler-test").toFile()
        realFactsDir.listFiles { f -> f.extension == "md" }?.forEach { it.copyTo(File(dir, it.name)) }
        extraFiles.forEach { it.copyTo(File(dir, it.name)) }
        return dir
    }

    @Test
    fun `real facts directory compiles without error, whatever it currently contains`() {
        val result = ContentCompiler.compile(realFactsDir)
        assertIs<CompileResult.Success>(result)
    }

    @Test
    fun `related links are mirrored symmetrically`() {
        // Pure unit test on synthetic FactOutput objects - no file parsing, no
        // category validation, no dependency on what's currently in the real
        // library. See ContentCompiler.mirrorRelated's doc comment for why.
        val a = FactOutput(
            id = "a", title = "A", categories = emptyList(), sources = emptyList(),
            notifyEligible = false, difficulty = null, related = listOf("b"), body = "", order = 1000,
        )
        val b = FactOutput(
            id = "b", title = "B", categories = emptyList(), sources = emptyList(),
            notifyEligible = false, difficulty = null, related = emptyList(), body = "", order = 2000,
        )

        val mirrored = ContentCompiler.mirrorRelated(listOf(a, b))

        assertContains(mirrored.first { it.id == "a" }.related, "b")
        assertContains(mirrored.first { it.id == "b" }.related, "a")
    }

    @Test
    fun `rejects an unlisted category`() {
        val dir = tempDirWith(File(invalidFixturesDir, "bad-category.md"))
        val result = ContentCompiler.compile(dir)
        assertIs<CompileResult.Failure>(result)
        assertTrue(result.errors.any { it.contains("unknown category") })
    }

    @Test
    fun `rejects duplicate ids`() {
        val dir = tempDirWith(
            File(invalidFixturesDir, "duplicate-id-a.md"),
            File(invalidFixturesDir, "duplicate-id-b.md"),
        )
        val result = ContentCompiler.compile(dir)
        assertIs<CompileResult.Failure>(result)
        assertTrue(result.errors.any { it.contains("duplicate id") })
    }

    @Test
    fun `rejects a dangling related reference`() {
        val dir = tempDirWith(File(invalidFixturesDir, "dangling-related.md"))
        val result = ContentCompiler.compile(dir)
        assertIs<CompileResult.Failure>(result)
        assertTrue(result.errors.any { it.contains("does not exist") })
    }

    @Test
    fun `rejects a fact with no sources`() {
        val dir = tempDirWith(File(invalidFixturesDir, "missing-sources.md"))
        val result = ContentCompiler.compile(dir)
        assertIs<CompileResult.Failure>(result)
        assertTrue(result.errors.any { it.contains("sources") })
    }

    @Test
    fun `rejects a non-string category element instead of crashing`() {
        // Regression test: unquoted YAML `no` parses as Boolean (YAML 1.1), which used
        // to pass an erased `as? List<String>` cast and throw ClassCastException later.
        val dir = tempDirWith(File(invalidFixturesDir, "non-string-category.md"))
        val result = ContentCompiler.compile(dir)
        assertIs<CompileResult.Failure>(result)
        assertTrue(result.errors.any { it.contains("must be a list of strings") })
    }

    @Test
    fun `rejects a non-map source element instead of crashing`() {
        // Regression test: a bare string in `sources` used to pass an erased
        // `as? List<Map<String, Any?>>` cast and throw ClassCastException later.
        val dir = tempDirWith(File(invalidFixturesDir, "non-map-source.md"))
        val result = ContentCompiler.compile(dir)
        assertIs<CompileResult.Failure>(result)
        assertTrue(result.errors.any { it.contains("must be a list of") && it.contains("title, url") })
    }

    @Test
    fun `serialized index always includes schemaVersion even at its default value`() {
        // Regression test: kotlinx.serialization silently omits a field that equals
        // its default unless encodeDefaults is set, which would defeat the whole
        // point of schemaVersion as an inspectable compatibility marker (decision 11).
        val index = FactIndexOutput(facts = emptyList())
        val json = outputJson.encodeToString(index)
        assertContains(json, "\"schemaVersion\"")
    }
}
