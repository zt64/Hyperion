plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    jvmToolchain(17)

    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.ktor.encoding)
                implementation(libs.brotli)
            }
        }
    }
}