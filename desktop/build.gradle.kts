import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    alias(libs.plugins.compose.jb)
    alias(libs.plugins.compose.compiler)
}

dependencies {
    implementation(projects.common)
    implementation(compose.material3)

    ktlintRuleset(libs.ktlint.compose.rules)
}

compose.desktop {
    application {
        mainClass = "dev.zt64.hyperion.MainKt"

        nativeDistributions {
            packageName = "hyperion"
            description = "YouTube client"
            packageVersion = rootProject.version.toString()
            licenseFile = rootProject.file("LICENSE.md")

            modules = arrayListOf(
                "java.base",
                "java.desktop",
                "java.instrument",
                "java.management",
                "java.prefs",
                "java.logging",
                "java.net.http"
            )

            targetFormats(
                TargetFormat.Dmg,
                TargetFormat.Deb,
                TargetFormat.AppImage,
                TargetFormat.Exe
            )
        }

        buildTypes.release.proguard {
            configurationFiles.from(file("proguard-rules.pro"))
            obfuscate = true
        }
    }
}