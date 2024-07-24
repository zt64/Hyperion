import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

dependencies {
    implementation(libs.tv.foundation)
    implementation(libs.tv.material)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        optIn.add("androidx.tv.material3.ExperimentalTvMaterial3Api")
    }
}