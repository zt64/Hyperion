@file:Suppress("UnstableApiUsage")

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
}

android {
    namespace = "com.zt.innertube"
    compileSdk = 33

    defaultConfig {
        minSdk = 21
        targetSdk = 33
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
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlinx.serialization.ExperimentalSerializationApi"
    }
}