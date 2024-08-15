package dev.zt64.hyperion.ui.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.zt64.hyperion.ui.LocalWindowSizeClass

/**
 * Adaptive top bar with [BackButton] as [navigationIcon]
 *
 * @param modifier
 * @param title
 * @param navigationIcon
 * @param actions
 * @param windowInsets
 * @param colors
 * @param scrollBehavior
 */
@Composable
fun AdaptiveTopBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = { },
    navigationIcon: @Composable () -> Unit = { BackButton() },
    actions: @Composable RowScope.() -> Unit = { },
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    val windowSizeClass = LocalWindowSizeClass.current

    if (windowSizeClass.heightSizeClass > WindowHeightSizeClass.Medium) {
        MediumTopAppBar(
            modifier = modifier,
            title = title,
            navigationIcon = navigationIcon,
            actions = actions,
            windowInsets = windowInsets,
            colors = colors,
            scrollBehavior = scrollBehavior
        )
    } else {
        TopAppBar(
            modifier = modifier,
            title = title,
            navigationIcon = navigationIcon,
            actions = actions,
            windowInsets = windowInsets,
            colors = colors,
            scrollBehavior = scrollBehavior
        )
    }
}

object AdaptiveTopAppBarDefaults {
    @Composable
    fun adaptiveScrollBehavior(
        state: TopAppBarState = rememberTopAppBarState(),
        canScroll: () -> Boolean = { true }
    ): TopAppBarScrollBehavior = actualAdaptiveScrollBehavior(state, canScroll)
}

@Composable
internal expect fun actualAdaptiveScrollBehavior(
    state: TopAppBarState,
    canScroll: () -> Boolean
): TopAppBarScrollBehavior