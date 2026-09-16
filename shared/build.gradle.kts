plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.sqldelight)
}

kotlin {
    jvmToolchain(17)
    jvm()
    androidTarget()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":taxonomy"))
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines.ext)
        }
        androidMain.dependencies {
            implementation(libs.sqldelight.android.driver)
        }
    }
}

sqldelight {
    databases {
        create("BitcoinFactsDatabase") {
            packageName.set("dev.emmett.bitcoinfacts.shared.db")
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
