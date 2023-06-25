plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    jvmToolchain(11)

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