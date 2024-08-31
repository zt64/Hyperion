@file:Suppress("UnstableApiUsage")

plugins {
    // alias(libs.plugins.kotlin.multiplatform)
    id("hyperion.kmp-configuration")
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvm()

    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.ktorBrotli)

                api(libs.serialization.json)
                api(libs.serialization.protobuf)

                implementation(libs.bundles.ktor)

                implementation(libs.datetime)
                implementation(libs.google.api.services.youtube)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.coroutines.test)
                implementation(libs.ktor.client.okhttp)
            }
        }

        all {
            languageSettings {
                optIn("kotlinx.serialization.ExperimentalSerializationApi")
            }
        }
    }
}