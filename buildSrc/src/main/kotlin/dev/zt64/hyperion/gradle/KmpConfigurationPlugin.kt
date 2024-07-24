package dev.zt64.hyperion.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KmpConfigurationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        configureKmp(target)
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    private fun configureKmp(target: Project) {
        target.apply {
            plugin("org.jetbrains.kotlin.multiplatform")
        }

        target.configure<KotlinMultiplatformExtension> {
            jvmToolchain(17)
            compilerOptions {
                freeCompilerArgs.addAll("-Xexpect-actual-classes", "-Xcontext-receivers")
            }
        }
    }
}