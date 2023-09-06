import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.moko.resources)
    alias(libs.plugins.compose)
    alias(libs.plugins.build.konfig)
}

group = "dev.zt64.hyperion"

@OptIn(ExperimentalKotlinGradlePluginApi::class)
kotlin {
    targetHierarchy.default()

    jvmToolchain(libs.versions.jvm.get().toInt())

    androidTarget()
    jvm("desktop")

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(projects.innertube)

                api(compose.ui)
                api(compose.foundation)
                api(compose.runtime)
                api(compose.animation)
                api(compose.animationGraphics)
                api(compose.materialIconsExtended)

                implementation(compose.material3)
                api(compose.uiTooling)
                api(compose.preview)

                api(libs.image.loader)
                api(libs.kotlinx.collections.immutable)

                api(libs.moko.resources.compose)

                api(libs.settings.noarg)
                api(libs.settings.coroutines)

                api(libs.paging.runtime.composeui)
                api(libs.paging.common)

                api(libs.napier)

                implementation(libs.bundles.ktor)
                implementation(libs.ktor.okhttp)
                implementation(libs.uuid)
                implementation(libs.koin.compose)
                implementation(libs.file.picker)
            }
        }

        named("androidMain") {
            dependsOn(commonMain)
            dependencies {
                api(libs.bundles.androidx)
                api(libs.bundles.media3)

                implementation(libs.compose.material3)

                api(libs.compose.shimmer)
                api(libs.compose.ui.tooling)

                api(libs.navigation)
                api(libs.koin.androidx.compose)
            }
        }

        named("desktopMain") {
            dependsOn(commonMain)
            dependencies {
                implementation(compose.desktop.currentOs)
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

android {
    namespace = "$group.common"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
    }
}

configurations.all {
    resolutionStrategy {
        dependencySubstitution {
            substitute(module("org.jetbrains.compose.material:material"))
                .using(module("org.jetbrains.compose.material3:material3:${libs.versions.compose.multiplatform}"))
                .because("Compose Material3 is used instead of Compose Material")
        }
    }
}

dependencies {
    debugImplementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.runtime.tracing)
}

multiplatformResources {
    multiplatformResourcesPackage = group.toString()
}

buildkonfig {
    packageName = group.toString()

    defaultConfigs {
        buildConfigField(STRING, "VERSION_NAME", version.toString())
        buildConfigField(BOOLEAN, "DEBUG", "false")
    }

    defaultConfigs("debug") {
        buildConfigField(BOOLEAN, "DEBUG", "true")
    }

    targetConfigs {
        android {

        }
    }
}