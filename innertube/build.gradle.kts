@file:Suppress("UnstableApiUsage")

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.library)
}

group = "dev.zt64.innertube"

android {
    namespace = "dev.zt64.innertube"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
    }
}

kotlin {
    jvmToolchain(libs.versions.jvm.get().toInt())

    androidTarget()
    jvm()

    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.ktorBrotli)

                api(libs.kotlinx.serialization.json)
                api(libs.kotlinx.serialization.protobuf)

                implementation(libs.bundles.ktor)

                compileOnly(libs.compose.stable.marker)
            }
        }

        all {
            languageSettings {
                optIn("kotlinx.serialization.ExperimentalSerializationApi")
            }
        }
    }
}