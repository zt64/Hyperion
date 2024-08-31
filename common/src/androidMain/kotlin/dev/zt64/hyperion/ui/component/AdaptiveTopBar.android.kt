package dev.zt64.hyperion.ui.component

import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.runtime.Composable
import dev.zt64.hyperion.ui.LocalWindowSizeClass

@Composable
internal actual fun actualAdaptiveScrollBehavior(state: TopAppBarState, canScroll: () -> Boolean): TopAppBarScrollBehavior {
    val windowSizeClass = LocalWindowSizeClass.current

    return if (windowSizeClass.heightSizeClass > WindowHeightSizeClass.Medium) {
        TopAppBarDefaults.pinnedScrollBehavior(
            state = state,
            canScroll = canScroll
        )
    } else {
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
            state = state,
            canScroll = canScroll
        )
    }
}