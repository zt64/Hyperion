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
version = "0.1.0"

@OptIn(ExperimentalKotlinGradlePluginApi::class)
kotlin {
    targetHierarchy.default()

    jvmToolchain(libs.versions.jvm.get().toInt())

    androidTarget()
    jvm("desktop")

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.material3)

                api(projects.innertube)

                api(compose.ui)
                api(compose.foundation)
                api(compose.runtime)
                api(compose.animation)
                api(compose.animationGraphics)
                api(compose.materialIconsExtended)

                api(compose.uiTooling)
                api(compose.preview)

                api(libs.image.loader)
                api(libs.kotlinx.collections.immutable)

                api(libs.moko.resources.compose.get().toString()) {
                    exclude(group = "org.jetbrains.compose.material", module ="material")
                }

                api(libs.settings.noarg)
                api(libs.settings.coroutines)

                api(libs.paging.compose.common)
                api(libs.paging.common)

                api(libs.napier)

                api(libs.window.size.multiplatform)

                api(libs.voyager.navigator)
                api(libs.voyager.tabNavigator)
                api(libs.voyager.transitions)
                api(libs.voyager.koin)

                implementation(libs.compose.shimmer)
                implementation(libs.material.kolor)
                implementation(libs.bundles.ktor)
                implementation(libs.ktor.okhttp)
                implementation(libs.uuid)
                api(libs.koin.compose)
                implementation(libs.file.picker)
                implementation(libs.color.picker)
                implementation("com.russhwolf:multiplatform-settings-test:1.1.0")
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.koin.test)
            }
        }

        named("androidMain") {
            dependsOn(commonMain)
            dependencies {
                api(libs.bundles.androidx)
                api(libs.bundles.media3)

                implementation(libs.compose.material3)

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
                optIn("androidx.compose.foundation.ExperimentalFoundationApi")
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
        }
    }
}

dependencies {
    debugApi(compose.uiTooling)
    debugApi(compose.preview)
    debugImplementation(libs.compose.runtime.tracing)
    debugImplementation("com.russhwolf:multiplatform-settings-test:1.1.0")
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