package dev.zt64.hyperion.gradle

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

class AndroidLibraryConfigurationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        configureAndroid(target)
    }

    private fun configureAndroid(target: Project) {
        target.apply(plugin = "com.android.library")

        target.configure<LibraryExtension> {
            compileSdk = 34
        }
    }
}