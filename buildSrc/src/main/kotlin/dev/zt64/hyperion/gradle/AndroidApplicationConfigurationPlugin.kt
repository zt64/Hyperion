package dev.zt64.hyperion.gradle

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConfigurationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        configureAndroid(target)
    }

    private fun configureAndroid(target: Project) {
        target.apply(plugin = "com.android.application")

        target.configure<ApplicationExtension> {
            namespace = "dev.zt64.hyperion"
            compileSdk = 34

            defaultConfig {
                minSdk = 21
                targetSdk = 34
                versionCode = 1
                versionName = target.version.toString()
            }

            buildTypes {
                release {
                    isMinifyEnabled = true
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt")
                        // parent!!.file("proguard-rules.pro")
                    )
                }

                all {
                    versionNameSuffix = "-${target.project.name}"
                }
            }

            packaging {
                resources {
                    // okhttp3 is used by some lib (no cookies so publicsuffixes.gz can be dropped)
                    excludes += "/okhttp3/**"
                    excludes += "META-INF/DEPENDENCIES"
                }
            }

            // dependencies {
            //     val implementation by configurations
            //
            //     implementation(rootProject.projects.common)
            //     ktlintRuleset(libs.ktlint.compose.rules)
            // }
        }

        target.configure<ApplicationAndroidComponentsExtension> {
            onVariants(selector().withBuildType("release")) {
                it.packaging.resources.excludes.addAll(
                    "/**/*.version",
                    "/kotlin-tooling-metadata.json",
                    "/DebugProbesKt.bin"
                )
            }
        }
        // Configure Android
    }
}