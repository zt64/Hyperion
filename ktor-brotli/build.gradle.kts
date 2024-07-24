import dev.zt64.hyperion.gradle.apple

plugins {
    `kmp-configuration`
}

group = "dev.zt64.ktor.brotli"

kotlin {
    jvm()
    apple(skipCheck = true)

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