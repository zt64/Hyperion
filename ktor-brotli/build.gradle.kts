plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

group = "dev.zt64.ktor.brotli"

kotlin {
    jvmToolchain(libs.versions.jvm.get().toInt())

    js {
        nodejs()
    }
    jvm()

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.ktor.encoding)
                implementation(libs.brotli)
            }
        }
    }
}