package dev.zt64.hyperion.ui.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.SliderColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

internal actual typealias SliderState = androidx.compose.material3.SliderPositions

internal actual val SliderState.activeRangeEnd: Float
    get() = activeRange.endInclusive

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