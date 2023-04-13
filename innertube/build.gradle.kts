@file:Suppress("UnstableApiUsage")

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.zt.innertube"
    compileSdk = 33

    defaultConfig {
        minSdk = 21
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

kotlin {
    jvmToolchain(11)

    android()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.ktorBrotli)

                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.serialization.protobuf)

                implementation(libs.ktor.core)
                implementation(libs.ktor.cio)
                implementation(libs.ktor.encoding)
                implementation(libs.ktor.contentnegotiation)
                implementation(libs.ktor.serialization.json)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.ktor.android)
            }
        }

        sourceSets.all {
            languageSettings {
                optIn("kotlinx.serialization.ExperimentalSerializationApi")
            }
        }
    }
}