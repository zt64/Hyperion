package dev.zt64.hyperion

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.zt64.hyperion.di.appModule
import org.koin.core.context.startKoin
import java.awt.Dimension

suspend fun main() {
    startKoin {
        modules(appModule)
    }

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Hyperion",
            icon = null
        ) {
            window.minimumSize = Dimension(800, 600)

            Hyperion()
        }
    }
}