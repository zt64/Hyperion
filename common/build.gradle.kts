import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.moko.resources)
    alias(libs.plugins.compose)
    alias(libs.plugins.buildKonfig)
}

kotlin {
    jvmToolchain(libs.versions.jvm.get().toInt())

    jvm("desktop")
    androidTarget()

    sourceSets {
        commonMain {
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
                api(compose.components.uiToolingPreview)

                api(libs.coil.compose.core)
                api(libs.coil.network.ktor)
                api(libs.kotlinx.collections.immutable)

                api(libs.moko.resources.compose.get().toString()) {
                    exclude(group = "org.jetbrains.compose.material", module = "material")
                }

                api(libs.settings.noarg)
                api(libs.settings.coroutines)

                api(libs.paging.compose.common)
                api(libs.paging.common)

                api(libs.napier)

                api(libs.windowSize)

                api(libs.voyager.navigator)
                api(libs.voyager.tabNavigator)
                api(libs.voyager.transitions)
                api(libs.voyager.koin)

                implementation(libs.compose.shimmer)
                implementation(libs.materialKolor)
                implementation(libs.bundles.ktor)
                implementation(libs.ktor.okhttp)
                implementation(libs.uuid)
                api(libs.koin.compose)
                implementation(libs.filePicker.get().toString()) {
                    exclude(group = "org.jetbrains.compose.material", module = "material")
                }
                implementation(libs.colorPicker.get().toString()) {
                    exclude(group = "org.jetbrains.compose.material", module = "material")
                }
                implementation(libs.settings.test)

                // implementation(libs.m3.adaptive)
                // implementation(libs.m3.adaptive.nav)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.koin.test)
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

        named("desktopMain") {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.coroutines.swing)
            }
        }

        all {
            languageSettings {
                enableLanguageFeature(LanguageFeature.ContextReceivers.name)
                optIn("androidx.compose.material3.ExperimentalMaterial3Api")
                optIn("androidx.compose.foundation.ExperimentalFoundationApi")
            }
        }
    }
}

android {
    namespace = "$group.hyperion.common"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs += "-Xexpect-actual-classes"
    }
}

compose {
    kotlinCompilerPlugin = "1.5.8.1-beta02"
    kotlinCompilerPluginArgs.addAll(
        "stabilityConfigurationPath=$projectDir/compose-stability.conf"
        // "reportsDestination=${project.layout.buildDirectory.get().asFile.absolutePath}/compose"
    )
}

dependencies {
    debugApi(compose.uiTooling)
    debugApi(compose.preview)
    debugImplementation(libs.settings.test)

    ktlintRuleset(libs.ktlint.compose.rules)
}

multiplatformResources {
    resourcesPackage.set("$group.hyperion")
}

buildkonfig {
    packageName = "$group.hyperion"

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