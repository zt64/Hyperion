@file:Suppress("UnstableApiUsage")

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
}

group = "dev.zt64.innertube"

kotlin {
    jvmToolchain(libs.versions.jvm.get().toInt())

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