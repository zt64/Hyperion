import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.compose)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(projects.common)
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
}

compose.desktop {
    application {
        mainClass = "dev.zt64.hyperion.MainKt"

        nativeDistributions {
            description = "YouTube client"
            licenseFile = rootProject.file("LICENSE.md")

            targetFormats(TargetFormat.Dmg, TargetFormat.Deb, TargetFormat.AppImage, TargetFormat.Exe)
        }

        buildTypes.release.proguard {
            obfuscate = true
        }
    }
}