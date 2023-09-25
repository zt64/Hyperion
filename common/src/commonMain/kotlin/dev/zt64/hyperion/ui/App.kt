package dev.zt64.hyperion.ui

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import dev.zt64.hyperion.di.*
import org.koin.compose.KoinApplication
import org.koin.core.KoinApplication

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun App(
    application: KoinApplication.() -> Unit = {},
    content: @Composable () -> Unit
) {
    KoinApplication(
        application = {
            modules(
                appModule,
                repositoryModule,
                httpModule,
                serviceModule,
                managerModule,
            )
            application()
        }
    ) {
        CompositionLocalProvider(
            LocalWindowSizeClass provides calculateWindowSizeClass(),
            content = content
        )
    }
}