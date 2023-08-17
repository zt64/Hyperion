plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.moko.resources)
    alias(libs.plugins.compose)
}

android {
    namespace = "dev.zt64.hyperion.common"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
    }

    buildFeatures {
        buildConfig = true
    }
}

configurations.all {
    resolutionStrategy.dependencySubstitution {
        substitute(module("org.jetbrains.compose.material:material"))
            .using(module("org.jetbrains.compose.material3:material3:${libs.versions.compose.multiplatform}"))
            .because("Compose Material3 is used instead of Compose Material")
    }
}

kotlin {
    jvmToolchain(17)

    androidTarget()
    jvm("desktop")

    sourceSets {
        commonMain {
            dependencies {
                api(projects.innertube)

                api(compose.ui)
                api(compose.foundation)
                api(compose.runtime)
                api(compose.animation)
                api(compose.animationGraphics)
                api(compose.materialIconsExtended)

                api(libs.image.loader)
                api(libs.kotlinx.collections.immutable)

                api(libs.moko.resources.compose)
                api(libs.moko.parcelize)

                api(libs.settings.noarg)
                api(libs.settings.coroutines)

                api(libs.paging.runtime.composeui)
                api(libs.paging.common)

                implementation(libs.bundles.ktor)
                implementation(libs.ktor.okhttp)
                implementation(libs.uuid)
                implementation(libs.koin.core)
                implementation(compose.material3)
                implementation(compose.uiTooling)
            }
        }

        named("androidMain") {
            dependsOn(commonMain.get())
            dependencies {
                api(libs.bundles.androidx)
                api(libs.bundles.media3)

                api(libs.compose.material3)
                api(libs.compose.material3.windowSizeClass)

                api(libs.compose.shimmer)
                api(libs.compose.ui.tooling)

                api(libs.navigation)
                api(libs.koin.compose)
                api(libs.accompanist.systemuicontroller)
            }
        }

        named("desktopMain") {
            dependsOn(commonMain.get())
            dependencies {
                implementation(compose.desktop.common)
            }
        }

        all {
            languageSettings {
                enableLanguageFeature("ContextReceivers")
                optIn("androidx.compose.material3.ExperimentalMaterial3Api")
            }
        }
    }
}

dependencies {
    debugImplementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.runtime.tracing)
}

multiplatformResources {
    multiplatformResourcesPackage = "dev.zt64.hyperion"
}
