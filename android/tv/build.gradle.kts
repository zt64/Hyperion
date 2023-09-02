import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

dependencies {
    implementation(libs.tv.foundation)
    implementation(libs.tv.material)
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs = listOf("-Xopt-in=androidx.tv.material3.ExperimentalTvMaterial3Api")
    }
}