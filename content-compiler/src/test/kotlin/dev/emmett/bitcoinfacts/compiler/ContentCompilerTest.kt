package dev.emmett.bitcoinfacts.compiler

import java.io.File
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ContentCompilerTest {

    private val realFactsDir = File(System.getProperty("contentFactsDir"))
    private val invalidFixturesDir = File(System.getProperty("invalidFixturesDir"))

    private fun tempDirWith(vararg extraFiles: File): File {
        val dir = Files.createTempDirectory("content-compiler-test").toFile()
        realFactsDir.listFiles { f -> f.extension == "md" }?.forEach { it.copyTo(File(dir, it.name)) }
        extraFiles.forEach { it.copyTo(File(dir, it.name)) }
        return dir
    }

    @Test
    fun `real facts compile successfully`() {
        val result = ContentCompiler.compile(realFactsDir)
        assertIs<CompileResult.Success>(result)
        assertTrue(result.facts.isNotEmpty())
    }

    @Test
    fun `related links are mirrored symmetrically`() {
        val result = ContentCompiler.compile(realFactsDir)
        assertIs<CompileResult.Success>(result)
        val fact001 = result.facts.first { it.id == "fact-001" }
        val fact002 = result.facts.first { it.id == "fact-002" }
        assertContains(fact001.related, "fact-002")
        assertContains(fact002.related, "fact-001")
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
}
