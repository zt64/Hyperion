plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

group = "dev.zt64"

kotlin {
    jvmToolchain(17)

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