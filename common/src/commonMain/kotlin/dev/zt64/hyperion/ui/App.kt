package dev.zt64.hyperion.ui

import androidx.compose.runtime.Composable
import dev.zt64.hyperion.di.commonModules
import org.koin.compose.KoinApplication
import org.koin.core.KoinApplication

@Composable
fun App(
    application: KoinApplication.() -> Unit = {},
    content: @Composable () -> Unit
) {
    KoinApplication(
        application = {
            commonModules()
            application()
        }
    ) {
        ProvideWindowSizeClass {
            content()
        }
    }
}