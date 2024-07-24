import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import org.jlleitschuh.gradle.ktlint.KtlintExtension

plugins {
    // Kotlin
    // alias(libs.plugins.kotlin.android) apply false
    // alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    // alias(libs.plugins.kotlin.parcelize) apply false

    // Android
    // alias(libs.plugins.android.application) apply false
    // alias(libs.plugins.android.library) apply false

    // Other
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.compose.jb) apply false
    alias(libs.plugins.moko.resources) apply false
    alias(libs.plugins.ktlint) apply false
}

allprojects {
    group = "dev.zt64.hyperion"
    version = "1.0.0"

    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    configure<KtlintExtension> {
        version = rootProject.libs.versions.ktlint
    }
}

subprojects {
    tasks.withType<KotlinJvmCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(rootProject.libs.versions.jvm.get()))
        }
    }
}