plugins {
    `kotlin-dsl`
}

fun DependencyHandler.implementation(dependency: Provider<PluginDependency>) {
    implementation(dependency.map { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" })
}

dependencies {
    implementation(libs.plugins.kotlin.multiplatform)
    implementation(libs.plugins.android.application)
}

gradlePlugin {
    plugins.register("kmp-configuration") {
        id = "hyperion.kmp-configuration"
        implementationClass = "dev.zt64.hyperion.gradle.KmpConfigurationPlugin"
    }

    plugins.register("android-application-configuration") {
        id = "hyperion.android-application"
        implementationClass = "dev.zt64.hyperion.gradle.AndroidApplicationConfigurationPlugin"
    }

    plugins.register("android-library-configuration") {
        id = "hyperion.android-library"
        implementationClass = "dev.zt64.hyperion.gradle.AndroidLibraryConfigurationPlugin"
    }
}