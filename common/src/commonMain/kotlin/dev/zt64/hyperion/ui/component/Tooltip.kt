package dev.zt64.hyperion.ui.component

import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TooltipScope
import androidx.compose.material3.TooltipState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.PopupPositionProvider

@Composable
fun PlainTooltipBox(
    tooltip: @Composable TooltipScope.() -> Unit,
    modifier: Modifier = Modifier,
    positionProvider: PopupPositionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
    state: TooltipState = rememberTooltipState(),
    content: @Composable () -> Unit
) {
    TooltipBox(
        positionProvider = positionProvider,
        state = state,
        tooltip = tooltip,
        modifier = modifier,
        content = content
    )
}

@Composable
fun RichTooltipBox(
    tooltip: @Composable TooltipScope.() -> Unit,
    modifier: Modifier = Modifier,
    positionProvider: PopupPositionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
    state: TooltipState = rememberTooltipState(),
    content: @Composable () -> Unit
) {
    TooltipBox(
        positionProvider = positionProvider,
        state = state,
        tooltip = tooltip,
        modifier = modifier,
        content = content
    )
}