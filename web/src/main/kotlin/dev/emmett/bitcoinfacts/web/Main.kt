package dev.emmett.bitcoinfacts.web

import dev.emmett.bitcoinfacts.compiler.CompileResult
import dev.emmett.bitcoinfacts.compiler.ContentCompiler
import java.io.File
import kotlin.system.exitProcess

/**
 * CLI entrypoint, wired as `:web:buildSite` (see web/build.gradle.kts):
 * `--input <content/facts> --output <site dir> --site-source <static assets>
 * --templates <dir> --base-url <absolute url> --app-download-url <url>`.
 */
fun main(args: Array<String>) {
    val options = parseArgs(args)
    val inputDir = File(options.getValue("input"))
    if (!inputDir.isDirectory) {
        System.err.println("Input directory does not exist: ${inputDir.absolutePath}")
        exitProcess(1)
    }
    val config = SiteConfig(
        baseUrl = options.getValue("base-url").let { if (it.endsWith("/")) it else "$it/" },
        appDownloadUrl = options.getValue("app-download-url"),
    )
    val ok = buildSite(
        inputDir = inputDir,
        outputDir = File(options.getValue("output")),
        siteSourceDir = File(options.getValue("site-source")),
        templatesDir = File(options.getValue("templates")),
        config = config,
    )
    if (!ok) exitProcess(1)
}

/**
 * Compiles the facts with the same [ContentCompiler] the app build uses, so any
 * content error fails the site build with the same messages - and deletes any
 * previous output, leaving no site behind for a deploy step to pick up.
 * Returns whether the site was generated.
 */
fun buildSite(inputDir: File, outputDir: File, siteSourceDir: File, templatesDir: File, config: SiteConfig): Boolean =
    when (val result = ContentCompiler.compile(inputDir)) {
        is CompileResult.Failure -> {
            outputDir.deleteRecursively()
            System.err.println("Content compiler found ${result.errors.size} error(s):")
            result.errors.forEach { System.err.println("  - $it") }
            false
        }
        is CompileResult.Success -> {
            SiteGenerator(config, siteSourceDir, templatesDir).generate(result.facts, outputDir)
            println("Generated site for ${result.facts.size} fact(s) -> ${outputDir.absolutePath} (${config.baseUrl})")
            true
        }
    }

private val REQUIRED = listOf("input", "output", "site-source", "templates", "base-url", "app-download-url")

private fun parseArgs(args: Array<String>): Map<String, String> {
    val options = mutableMapOf<String, String>()
    var i = 0
    while (i < args.size) {
        val arg = args[i]
        if (arg.startsWith("--") && i + 1 < args.size) {
            options[arg.removePrefix("--")] = args[i + 1]
            i += 2
        } else {
            i += 1
        }
    }
    for (name in REQUIRED) {
        require(name in options) { "Missing required --$name argument" }
    }
    return options
}
