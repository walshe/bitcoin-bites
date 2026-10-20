plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    application
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("dev.emmett.bitcoinfacts.web.MainKt")
}

dependencies {
    implementation(project(":content-compiler"))
    implementation(project(":taxonomy"))
    implementation(libs.kotlinx.serialization.json)

    testImplementation(kotlin("test-junit5"))
}

tasks.test {
    useJUnitPlatform()
    systemProperty("contentFactsDir", rootProject.file("content/facts").absolutePath)
    systemProperty("invalidFixturesDir", rootProject.file("content/test-fixtures/invalid").absolutePath)
    systemProperty("siteSourceDir", file("src/main/site").absolutePath)
    systemProperty("templatesDir", file("src/main/templates").absolutePath)
}

/**
 * Absolute URL the site is served from (with trailing slash). Only canonical,
 * Open Graph and sitemap URLs use it - every internal link is relative. CI
 * passes the value `actions/configure-pages` reports; see design.md decision 8.
 */
val siteBaseUrl = providers.gradleProperty("siteBaseUrl")
    .orElse("https://walshe.github.io/bitcoin-bites/")

/** Target of the site's "Get the Android app" link (design.md decision 13). */
val appDownloadUrl = providers.gradleProperty("appDownloadUrl")
    .orElse("https://github.com/walshe/bitcoin-bites/releases/latest/download/bitcoin-bites.apk")

val buildSite = tasks.register<JavaExec>("buildSite") {
    group = "build"
    description = "Generates the static GitHub Pages site from content/facts into build/site"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set(application.mainClass)

    val inputDir = rootProject.file("content/facts")
    val siteSourceDir = file("src/main/site")
    val templatesDir = file("src/main/templates")
    val outputDir = layout.buildDirectory.dir("site")
    inputs.dir(inputDir)
    inputs.dir(siteSourceDir)
    inputs.dir(templatesDir)
    inputs.property("siteBaseUrl", siteBaseUrl)
    inputs.property("appDownloadUrl", appDownloadUrl)
    outputs.dir(outputDir)

    doFirst {
        args = listOf(
            "--input", inputDir.absolutePath,
            "--output", outputDir.get().asFile.absolutePath,
            "--site-source", siteSourceDir.absolutePath,
            "--templates", templatesDir.absolutePath,
            "--base-url", siteBaseUrl.get(),
            "--app-download-url", appDownloadUrl.get(),
        )
    }
}
