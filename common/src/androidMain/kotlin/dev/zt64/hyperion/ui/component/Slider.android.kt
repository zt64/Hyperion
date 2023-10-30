package dev.zt64.hyperion.ui.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.SliderColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

internal actual typealias SliderState = androidx.compose.material3.SliderState

internal actual val SliderState.activeRangeEnd: Float
    get() = value

@Composable
internal actual fun Slider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    onValueChangeFinished: (() -> Unit)?,
    colors: SliderColors,
    interactionSource: MutableInteractionSource,
    steps: Int,
    thumb: @Composable (SliderState) -> Unit,
    track: @Composable (SliderState) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>
) = androidx.compose.material3.Slider(
    value = value,
    onValueChange = onValueChange,
    modifier = modifier,
    enabled = enabled,
    onValueChangeFinished = onValueChangeFinished,
    colors = colors,
    interactionSource = interactionSource,
    steps = steps,
    thumb = thumb,
    track = track,
    valueRange = valueRange
)

@Composable
internal actual fun SliderColors.commonTrackColor(enabled: Boolean, active: Boolean): Color {
    return if (enabled) {
        if (active) activeTrackColor else inactiveTrackColor
    } else {
        if (active) disabledActiveTrackColor else disabledInactiveTrackColor
    }
}