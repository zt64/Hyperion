package dev.zt64.hyperion.ui.component

// internal actual typealias SliderState = androidx.compose.material3.SliderState
//
// internal actual val SliderState.activeRangeEnd: Float
//     get() = value
//
// @Composable
// internal actual fun Slider(
//     value: Float,
//     onValueChange: (Float) -> Unit,
//     onValueChangeFinished: (() -> Unit)?,
//     interactionSource: MutableInteractionSource,
//     track: @Composable (SliderState) -> Unit,
//     modifier: Modifier,
//     enabled: Boolean,
//     colors: SliderColors,
//     steps: Int,
//     thumb: @Composable (SliderState) -> Unit,
//     valueRange: ClosedFloatingPointRange<Float>
// ) = androidx.compose.material3.Slider(
//     value = value,
//     onValueChange = onValueChange,
//     modifier = modifier,
//     enabled = enabled,
//     onValueChangeFinished = onValueChangeFinished,
//     colors = colors,
//     interactionSource = interactionSource,
//     steps = steps,
//     thumb = thumb,
//     track = track,
//     valueRange = valueRange
// )
//
// @Composable
// internal actual fun SliderColors.commonTrackColor(
//     enabled: Boolean,
//     active: Boolean
// ): Color {
//     return if (enabled) {
//         if (active) activeTrackColor else inactiveTrackColor
//     } else {
//         if (active) disabledActiveTrackColor else disabledInactiveTrackColor
//     }
// }