import dev.zt64.hyperion.gradle.apple

plugins {
    id("hyperion.kmp-configuration")
}

group = "dev.zt64.ktor.brotli"
description = """
    Brotli encoder/decoder for Ktor. Only JVM is supported, which may cause issues when native targets are added.
    https://github.com/google/brotli/issues/1123 for multiplatform brotli
""".trimIndent()

kotlin {
    jvm()
    apple()

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.ktor.encoding)
            }
        }

        jvmMain {
            dependencies {
                implementation(libs.brotli)
            }
        }
    }
}