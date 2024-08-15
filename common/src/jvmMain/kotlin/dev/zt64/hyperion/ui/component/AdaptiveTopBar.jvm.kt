package dev.zt64.hyperion.ui.component

import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.Composable

@Composable
internal actual fun actualAdaptiveScrollBehavior(
    state: TopAppBarState,
    canScroll: () -> Boolean
): TopAppBarScrollBehavior {
    return TopAppBarDefaults.pinnedScrollBehavior(
        state = state,
        canScroll = canScroll
    )
}