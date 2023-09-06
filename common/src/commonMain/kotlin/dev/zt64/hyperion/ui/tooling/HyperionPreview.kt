package dev.zt64.hyperion.ui.tooling

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.zt64.hyperion.BuildKonfig

@Composable
internal fun HyperionPreview(
    isDarkTheme: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    LaunchedEffect(Unit) {
        require(!BuildKonfig.DEBUG) { "HyperionPreview should not be used in production" }
    }

    MaterialTheme(
        colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
                content = content
            )
        }
    }
}