plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
}

kotlin {
    jvmToolchain(17)
    jvm()
    androidTarget()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":taxonomy"))
        }
    }
}

android {
    namespace = "dev.emmett.bitcoinfacts.shared"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
