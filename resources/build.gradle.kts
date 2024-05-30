import dev.icerock.gradle.MRVisibility
import dev.zt64.hyperion.gradle.apple

plugins {
    `kmp-configuration`
    `android-library`

    alias(libs.plugins.moko.resources)
}

kotlin {
    jvm()
    androidTarget()
    apple()

    sourceSets {
        commonMain {
            dependencies {
                api(libs.moko.resources.compose)
            }
        }
    }
}

android {
    namespace = "dev.zt64.hyperion"
}

multiplatformResources {
    resourcesPackage = "dev.zt64.hyperion"
    resourcesVisibility = MRVisibility.Public
}