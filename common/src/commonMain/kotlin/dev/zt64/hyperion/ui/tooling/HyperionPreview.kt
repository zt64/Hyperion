package dev.zt64.hyperion.ui.tooling

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import dev.zt64.hyperion.BuildKonfig
import dev.zt64.hyperion.di.appModule
import dev.zt64.hyperion.di.httpModule
import dev.zt64.hyperion.domain.manager.PreferencesManager
import dev.zt64.hyperion.domain.manager.PreferencesManagerImpl
import dev.zt64.hyperion.ui.ProvideWindowSizeClass
import org.koin.compose.KoinApplication
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun HyperionPreview(modifier: Modifier = Modifier, isDarkTheme: Boolean = true, content: @Composable BoxScope.() -> Unit) {
    LaunchedEffect(Unit) {
        require(!BuildKonfig.DEBUG) { "HyperionPreview should not be used in production" }
    }

    KoinApplication(
        application = {
            modules(appModule, httpModule)
            modules(
                module {
                    singleOf(::PreferencesManagerImpl) bind PreferencesManager::class
                }
            )
        }
    ) {
        ProvideWindowSizeClass(
            WindowSizeClass.calculateFromSize(
                size = Size(1920f, 1080f),
                density = LocalDensity.current
            )
        ) {
            MaterialTheme(
                colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()
            ) {
                Surface(modifier) {
                    Box(
                        contentAlignment = Alignment.Center,
                        content = content
                    )
                }
            }
        }
    }
}