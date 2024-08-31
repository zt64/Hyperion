@file:Suppress("UnstableApiUsage")

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

plugins {
    id("hyperion.android-application") apply false
}

subprojects {
    val libs = rootProject.libs

    apply {
        plugin("hyperion.android-application")
        plugin(libs.plugins.kotlin.android.get().pluginId)
        plugin(libs.plugins.android.application.get().pluginId)
        plugin(libs.plugins.compose.compiler.get().pluginId)
    }

    with(kotlinExtension) {
        sourceSets.all {
            languageSettings {
                enableLanguageFeature(LanguageFeature.ContextReceivers.name)
                optIn("androidx.compose.material3.ExperimentalMaterial3Api")
                optIn("androidx.compose.foundation.ExperimentalFoundationApi")
            }
        }

        jvmToolchain(libs.versions.jvm.get().toInt())
    }

    configure<ApplicationExtension> {
        namespace = "dev.zt64.hyperion"

        defaultConfig {
            versionCode = 1
            versionName = version.toString()
        }

        buildTypes {
            release {
                isMinifyEnabled = true
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    parent!!.file("proguard-rules.pro")
                )
            }

            all {
                versionNameSuffix = "-${project.name}"
            }
        }

        packaging {
            resources {
                pickFirsts.add("META-INF/*")
                // excludes.addAll(
                //     listOf(
                //         // okhttp3 is used by some lib (no cookies so publicsuffixes.gz can be dropped)
                //         "/okhttp3/**",
                //         "/META-INF/DEPENDENCIES",
                //         "/META-INF/INDEX.LIST"
                //     )
                // )
            }
        }

        dependencies {
            val implementation by configurations

            implementation(rootProject.projects.common)
            ktlintRuleset(libs.ktlint.compose.rules)
        }
    }

    configure<ApplicationAndroidComponentsExtension> {
        onVariants(selector().withBuildType("release")) {
            it.packaging.resources.excludes.addAll(
                "/**/*.version",
                "/kotlin-tooling-metadata.json",
                "/DebugProbesKt.bin"
            )
        }
    }
}