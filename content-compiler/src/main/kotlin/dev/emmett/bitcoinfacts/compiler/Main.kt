package dev.emmett.bitcoinfacts.compiler

import kotlinx.serialization.encodeToString
import java.io.File
import kotlin.system.exitProcess

/**
 * CLI entrypoint: `--input <content/facts dir> --output <facts.json path>`.
 * Wired as a Gradle task (see androidApp/build.gradle.kts) that runs before the
 * app build and fails it on any validation error.
 */
fun main(args: Array<String>) {
    val options = parseArgs(args)
    val inputDir = File(options.getValue("input"))
    val outputFile = File(options.getValue("output"))

    if (!inputDir.isDirectory) {
        System.err.println("Input directory does not exist: ${inputDir.absolutePath}")
        exitProcess(1)
    }

    when (val result = ContentCompiler.compile(inputDir)) {
        is CompileResult.Failure -> {
            System.err.println("Content compiler found ${result.errors.size} error(s):")
            result.errors.forEach { System.err.println("  - $it") }
            exitProcess(1)
        }
        is CompileResult.Success -> {
            outputFile.parentFile?.mkdirs()
            val index = FactIndexOutput(facts = result.facts)
            outputFile.writeText(outputJson.encodeToString(index))
            println("Compiled ${result.facts.size} fact(s) -> ${outputFile.absolutePath}")
        }
    }
}

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
    require("input" in options) { "Missing required --input <dir> argument" }
    require("output" in options) { "Missing required --output <file> argument" }
    return options
}
