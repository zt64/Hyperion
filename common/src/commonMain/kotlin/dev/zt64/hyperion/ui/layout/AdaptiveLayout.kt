package dev.zt64.hyperion.ui.layout

import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import dev.zt64.hyperion.ui.LocalWindowSizeClass

@Composable
fun AdaptiveLayout(content: @Composable (width: WindowWidthSizeClass, height: WindowHeightSizeClass) -> Unit) {
    val windowSizeClass = LocalWindowSizeClass.current

    content(windowSizeClass.widthSizeClass, windowSizeClass.heightSizeClass)
}