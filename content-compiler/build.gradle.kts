plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    application
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("dev.emmett.bitcoinfacts.compiler.MainKt")
}

dependencies {
    implementation(project(":taxonomy"))
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.snakeyaml)

    testImplementation(kotlin("test-junit5"))
}

tasks.test {
    useJUnitPlatform()
    systemProperty("contentFactsDir", rootProject.file("content/facts").absolutePath)
    systemProperty("invalidFixturesDir", rootProject.file("content/test-fixtures/invalid").absolutePath)
}
