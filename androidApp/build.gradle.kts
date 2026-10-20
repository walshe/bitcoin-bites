import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

kotlin {
    jvmToolchain(17)
}

// Release signing lives outside version control (keystore.properties is gitignored -
// see keystore.properties.example). Loaded conditionally so a clone without it can
// still build/run debug; only `bundleRelease`/`assembleRelease` need it populated.
val keystoreProperties = Properties().apply {
    val file = rootProject.file("keystore.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

android {
    namespace = "dev.emmett.bitcoinfacts.android"
    compileSdk = 36

    defaultConfig {
        applicationId = "dev.emmett.bitcoinfacts.android"
        minSdk = 24
        // Play requires new apps/updates to target API 36 as of 2026-08-31.
        targetSdk = 36
        versionCode = 5
        versionName = "1.1"
    }

    signingConfigs {
        if (keystoreProperties.containsKey("storeFile")) {
            create("release") {
                storeFile = file(keystoreProperties.getProperty("storeFile"))
                storePassword = keystoreProperties.getProperty("storePassword")
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            if (keystoreProperties.containsKey("storeFile")) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

/**
 * Declares the generated-assets directory as a real task output, so AGP can wire
 * every consumer of it automatically. Plain `JavaExec` has no `DirectoryProperty`
 * to hand to `addGeneratedSourceDirectory`, hence the subclass.
 */
abstract class CompileFactsTask : JavaExec() {
    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty
}

val compileFacts = tasks.register<CompileFactsTask>("compileFacts") {
    group = "build"
    description = "Compiles content/facts/*.md into the bundled facts.json runtime index (task 2.3-2.6)"
    dependsOn(":content-compiler:classes")
    classpath = project(":content-compiler")
        .extensions.getByType<JavaPluginExtension>()
        .sourceSets.getByName("main").runtimeClasspath
    mainClass.set("dev.emmett.bitcoinfacts.compiler.MainKt")

    val inputDir = rootProject.file("content/facts")
    inputs.dir(inputDir)
    outputDir.set(layout.buildDirectory.dir("generated/assets/facts"))

    doFirst {
        args = listOf(
            "--input", inputDir.absolutePath,
            "--output", outputDir.get().file("facts.json").asFile.absolutePath,
        )
    }
}

// Registering through the variant API (rather than `sourceSets.assets.srcDir` plus a
// hand-maintained `dependsOn` list) means every task that reads assets picks up the
// dependency - including the lint model tasks, which Gradle 8.13 fails the build over
// if they read an undeclared task output.
androidComponents {
    onVariants { variant ->
        variant.sources.assets?.addGeneratedSourceDirectory(compileFacts, CompileFactsTask::outputDir)
    }
}

dependencies {
    implementation(project(":shared"))

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    debugImplementation(libs.compose.ui.tooling)

    implementation(libs.activity.compose)
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.navigation.compose)
    implementation(libs.work.runtime.ktx)
}
