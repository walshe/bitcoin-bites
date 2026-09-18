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
    compileSdk = 34

    defaultConfig {
        applicationId = "dev.emmett.bitcoinfacts.android"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "0.1"
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

    sourceSets {
        getByName("main") {
            assets.srcDir(layout.buildDirectory.dir("generated/assets/facts"))
        }
    }
}

val compileFacts = tasks.register<JavaExec>("compileFacts") {
    group = "build"
    description = "Compiles content/facts/*.md into the bundled facts.json runtime index (task 2.3-2.6)"
    dependsOn(":content-compiler:classes")
    classpath = project(":content-compiler")
        .extensions.getByType<JavaPluginExtension>()
        .sourceSets.getByName("main").runtimeClasspath
    mainClass.set("dev.emmett.bitcoinfacts.compiler.MainKt")

    val inputDir = rootProject.file("content/facts")
    val outputFile = layout.buildDirectory.file("generated/assets/facts/facts.json")

    inputs.dir(inputDir)
    outputs.file(outputFile)

    doFirst {
        args = listOf(
            "--input", inputDir.absolutePath,
            "--output", outputFile.get().asFile.absolutePath,
        )
    }
}

tasks.matching { it.name.startsWith("merge") && it.name.endsWith("Assets") }.configureEach {
    dependsOn(compileFacts)
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
