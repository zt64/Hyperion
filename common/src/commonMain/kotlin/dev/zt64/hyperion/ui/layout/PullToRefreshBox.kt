package dev.zt64.hyperion.ui.layout

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Multiplatform variant of PullToRefreshBox that disables the pull-to-refresh functionality on
 * desktop platforms, and delegates to the Android implementation on Android.
 *
 * @param isRefreshing
 * @param onRefresh
 * @param modifier
 * @param state
 * @param contentAlignment
 * @param indicator
 * @param content
 */
@Composable
expect inline fun PullToRefreshBox(
    isRefreshing: Boolean,
    noinline onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    state: PullToRefreshState = rememberPullToRefreshState(),
    contentAlignment: Alignment = Alignment.TopStart,
    noinline indicator: @Composable BoxScope.() -> Unit = {
        Indicator(
            modifier = Modifier.align(Alignment.TopCenter),
            isRefreshing = isRefreshing,
            state = state
        )
    },
    noinline content: @Composable BoxScope.() -> Unit
)