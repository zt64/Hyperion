package dev.zt64.hyperion.ui.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

expect class SliderState

internal expect val SliderState.activeRangeEnd: Float

@Composable
internal expect fun SliderColors.commonTrackColor(enabled: Boolean, active: Boolean): Color

@Composable
internal expect fun Slider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier,
    enabled: Boolean = true,
    onValueChangeFinished: (() -> Unit)?,
    colors: SliderColors = SliderDefaults.colors(),
    interactionSource: MutableInteractionSource,
    steps: Int = 0,
    thumb: @Composable (SliderState) -> Unit = {
        Thumb(
            interactionSource = interactionSource,
            colors = colors
        )
    },
    track: @Composable (SliderState) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f
)