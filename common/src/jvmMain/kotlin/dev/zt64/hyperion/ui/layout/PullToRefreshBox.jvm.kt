package dev.zt64.hyperion.ui.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Suppress("NOTHING_TO_INLINE")
@Composable
actual inline fun PullToRefreshBox(
    isRefreshing: Boolean,
    noinline onRefresh: () -> Unit,
    modifier: Modifier,
    state: PullToRefreshState,
    contentAlignment: Alignment,
    noinline indicator: @Composable BoxScope.() -> Unit,
    noinline content: @Composable BoxScope.() -> Unit
) {
    Box {
        content()
    }
}