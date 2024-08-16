import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import dev.zt64.hyperion.gradle.apple
import dev.zt64.hyperion.gradle.excludeMaterial2
import org.jetbrains.compose.resources.ResourcesExtension.ResourceClassGeneration
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    id("hyperion.kmp-configuration")
    id("hyperion.android-library")
    // alias(libs.plugins.kotlin.multiplatform)
    // alias(libs.plugins.android.library)
    // alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.compose.jb)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.buildKonfig)
}

kotlin {
    jvmToolchain(libs.versions.jvm.get().toInt())

    jvm()
    androidTarget()
    apple()

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    applyDefaultHierarchyTemplate {
        common {
            group("jvmCommon") {
                withAndroidTarget()
                withJvm()
            }
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.api)
                api(projects.resources)

                // Compose
                implementation(compose.material3)
                api(compose.ui)
                api(compose.foundation)
                api(compose.runtime)
                api(compose.animation)
                api(compose.animationGraphics)
                api(compose.materialIconsExtended)
                compileOnly(compose.uiTooling)
                api(libs.bundles.coil)
                api(libs.bundles.voyager)
                api(libs.paging.compose.common)
                api(libs.windowSize)
                api(libs.koin.compose)
                implementation(libs.reorderable)
                implementation(libs.compose.shimmer)
                implementation(libs.materialKolor)
                implementation(libs.fileKit)
                implementation(libs.colorPicker.get().toString()) {
                    excludeMaterial2()
                }
                // implementation(libs.m3.adaptive)
                // implementation(libs.m3.adaptive.nav)

                // TODO: Switch to DataStore or other alternative
                api(libs.settings.noarg)
                api(libs.settings.coroutines)

                // Networking & API
                implementation(libs.bundles.ktor)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.dearrow)
                implementation(libs.ryd)
                api(libs.firebase.messaging)

                // Misc
                api(libs.immutableCollections)
                api(libs.napier)
                implementation(libs.uuid)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.koin.test)
                implementation(libs.settings.test)
            }
        }

        androidMain {
            dependencies {
                api(libs.bundles.androidx)
                api(libs.bundles.media3)

                implementation(libs.compose.m3)

                api(libs.koin.androidx.compose)
            }
        }

        jvmMain {
            dependencies {
                implementation(compose.desktop.currentOs) {
                    excludeMaterial2()
                }
                implementation(libs.coroutines.swing)
            }
        }

        all {
            languageSettings {
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

compose {
    resources {
        generateResClass = ResourceClassGeneration.Never
    }
}

composeCompiler {
    stabilityConfigurationFile = project.file("compose-stability.conf")
    reportsDestination = project.layout.buildDirectory.dir("compose")
}

dependencies {
    debugApi(compose.uiTooling)
    debugApi(compose.preview)
    debugImplementation(libs.settings.test)

    ktlintRuleset(libs.ktlint.compose.rules)
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

ktlint {
    filter {
        // I have zero idea why the other way doesn't work
        exclude { it.file.path.contains("generated") }
    }
}