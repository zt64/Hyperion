package dev.zt64.hyperion.ui

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf

val LocalWindowSizeClass = compositionLocalOf<WindowSizeClass> {
    error("WindowSizeClass not provided")
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
internal fun ProvideWindowSizeClass(windowSizeClass: WindowSizeClass = calculateWindowSizeClass(), content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass, content)
}