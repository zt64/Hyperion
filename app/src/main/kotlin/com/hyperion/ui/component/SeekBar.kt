package com.hyperion.ui.component

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min
import kotlin.time.Duration

@Composable
fun SeekBar(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    value: Float,
    duration: Duration,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    onSeek: (Float) -> Unit,
    onSeekFinished: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val onValueChangeState = rememberUpdatedState<(Float) -> Unit> {
        if (it != value) onSeek(it)
    }

    BoxWithConstraints(
        modifier = modifier.focusable(enabled, interactionSource)
    ) {
        val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
        val widthPx = constraints.maxWidth.toFloat()
        val maxPx: Float
        val minPx: Float
        val thumbRadius = 12.dp

        with(LocalDensity.current) {
            maxPx = max(widthPx - thumbRadius.toPx(), 0f)
            minPx = min(thumbRadius.toPx(), maxPx)
        }

        fun scaleToOffset(userValue: Float) = scale(valueRange.start, valueRange.endInclusive, userValue, minPx, maxPx)

        val rawOffset: MutableState<Float> = remember { mutableStateOf(scaleToOffset(value)) }
        val pressOffset = remember { mutableStateOf(0f) }

        val draggableState = remember(minPx, maxPx, valueRange) {
            SliderDraggableState {
                rawOffset.value = (rawOffset.value + it + pressOffset.value)
                pressOffset.value = 0f
                onValueChangeState.value(rawOffset.value)
            }
        }

        val gestureEndAction = rememberUpdatedState {
            // check isDragging in case the change is still in progress (touch -> drag case)
            if (!draggableState.isDragging) onSeekFinished?.invoke()
        }

        val coerced = value.coerceIn(valueRange.start, valueRange.endInclusive)
        val fraction = calcFraction(valueRange.start, valueRange.endInclusive, coerced)

        SliderImpl(
            modifier = Modifier
                .sliderTapModifier(
                    draggableState = draggableState,
                    interactionSource = interactionSource,
                    maxPx = widthPx,
                    isRtl = isRtl,
                    rawOffset = rawOffset,
                    gestureEndAction = gestureEndAction,
                    pressOffset = pressOffset,
                    enabled = enabled
                )
                .draggable(
                    orientation = Orientation.Horizontal,
                    reverseDirection = isRtl,
                    enabled = enabled,
                    interactionSource = interactionSource,
                    onDragStopped = { gestureEndAction.value() },
                    startDragImmediately = draggableState.isDragging,
                    state = draggableState
                ),
            enabled = enabled,
            positionFraction = fraction,
            width = maxPx - minPx,
            interactionSource = interactionSource,
        )
    }
}

@Composable
private fun SliderImpl(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    positionFraction: Float,
    width: Float,
    interactionSource: MutableInteractionSource
) {
    Box(
        modifier = modifier.then(
            Modifier.heightIn(max = 48.dp)
        )
    ) {
        val trackStrokeWidth: Float = with(LocalDensity.current) { 6.dp.toPx() }

        Track(
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            positionFractionStart = 0f,
            positionFractionEnd = positionFraction,
            thumbWidth = 20.dp,
            trackStrokeWidth = trackStrokeWidth
        )
//        SliderThumb(
//            offset = widthDp * positionFraction,
//            interactionSource = interactionSource,
//            enabled = enabled,
//            thumbSize = DpSize(20.dp, 20.dp)
//        )
    }
}

@Composable
private fun Track(
    modifier: Modifier,
    colors: SliderColors = SliderDefaults.colors(),
    enabled: Boolean,
    positionFractionStart: Float,
    positionFractionEnd: Float,
    thumbWidth: Dp,
    trackStrokeWidth: Float
) {
    val thumbRadiusPx = with(LocalDensity.current) { thumbWidth.toPx() / 2 }
    val inactiveTrackColor = colors.trackColor(enabled, active = false)
    val activeTrackColor = colors.trackColor(enabled, active = true)
    Canvas(modifier) {
        val isRtl = layoutDirection == LayoutDirection.Rtl
        val sliderLeft = Offset(0f, center.y)
        val sliderRight = Offset(size.width, center.y)
        val sliderStart = if (isRtl) sliderRight else sliderLeft
        val sliderEnd = if (isRtl) sliderLeft else sliderRight
        drawLine(
            color = inactiveTrackColor.value,
            start = sliderStart,
            end = sliderEnd,
            strokeWidth = trackStrokeWidth
        )

        drawLine(
            color = activeTrackColor.value,
            start = Offset(
                x = sliderStart.x + (sliderEnd.x - sliderStart.x) * positionFractionStart,
                y = center.y
            ),
            end = Offset(
                x = sliderStart.x + (sliderEnd.x - sliderStart.x) * positionFractionEnd,
                y = center.y
            ),
            strokeWidth = trackStrokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun BoxScope.SliderThumb(
    offset: Dp,
    interactionSource: MutableInteractionSource,
    colors: SliderColors = SliderDefaults.colors(),
    enabled: Boolean,
    thumbSize: DpSize
) {
    Box(
        Modifier
            .padding(start = offset)
            .align(Alignment.CenterStart)
    ) {
        val interactions = remember { mutableStateListOf<Interaction>() }
        LaunchedEffect(interactionSource) {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> interactions.add(interaction)
                    is PressInteraction.Release -> interactions.remove(interaction.press)
                    is PressInteraction.Cancel -> interactions.remove(interaction.press)
                    is DragInteraction.Start -> interactions.add(interaction)
                    is DragInteraction.Stop -> interactions.remove(interaction.start)
                    is DragInteraction.Cancel -> interactions.remove(interaction.start)
                }
            }
        }

        val elevation = if (interactions.isNotEmpty()) 6.dp else 1.dp
        val shape = CircleShape

        Spacer(
            modifier = Modifier
                .size(thumbSize)
                .indication(
                    interactionSource = interactionSource,
                    indication = rememberRipple(
                        bounded = false,
                        radius = 20.dp
                    )
                )
                .hoverable(interactionSource = interactionSource)
                .shadow(
                    elevation = if (enabled) elevation else 0.dp,
                    shape = shape,
                    clip = false
                )
                .background(
                    color = colors.thumbColor(enabled).value,
                    shape = shape
                )
        )
    }
}

private class SliderDraggableState(
    val onDelta: (Float) -> Unit
) : DraggableState {
    var isDragging by mutableStateOf(false)
        private set

    private val dragScope = object : DragScope {
        override fun dragBy(pixels: Float) = onDelta(pixels)
    }

    private val scrollMutex = MutatorMutex()

    override suspend fun drag(
        dragPriority: MutatePriority,
        block: suspend DragScope.() -> Unit
    ) = coroutineScope {
        isDragging = true
        scrollMutex.mutateWith(dragScope, dragPriority, block)
        isDragging = false
    }

    override fun dispatchRawDelta(delta: Float) = onDelta(delta)
}

// Scale x1 from a1..b1 range to a2..b2 range
private fun scale(a1: Float, b1: Float, x1: Float, a2: Float, b2: Float) =
    lerp(a2, b2, calcFraction(a1, b1, x1))

// Calculate the 0..1 fraction that `pos` value represents between `a` and `b`
private fun calcFraction(a: Float, b: Float, pos: Float) =
    (if (b - a == 0f) 0f else (pos - a) / (b - a)).coerceIn(0f, 1f)

private fun Modifier.sliderTapModifier(
    draggableState: DraggableState,
    interactionSource: MutableInteractionSource,
    maxPx: Float,
    isRtl: Boolean,
    rawOffset: State<Float>,
    gestureEndAction: State<() -> Unit>,
    pressOffset: MutableState<Float>,
    enabled: Boolean
) = composed {
    if (enabled) {
        val scope = rememberCoroutineScope()

        pointerInput(draggableState, interactionSource, maxPx, isRtl) {
            detectTapGestures(
                onPress = { pos ->
                    val to = if (isRtl) maxPx - pos.x else pos.x
                    pressOffset.value = to - rawOffset.value
                    try {
                        awaitRelease()
                    } catch (_: GestureCancellationException) {
                        pressOffset.value = 0f
                    }
                },
                onTap = {
                    scope.launch {
                        draggableState.drag(MutatePriority.UserInput) {
                            // just trigger animation, press offset will be applied
                            dragBy(0f)
                        }
                        gestureEndAction.value()
                    }
                }
            )
        }
    } else this
}

fun lerp(start: Float, stop: Float, fraction: Float) = (1 - fraction) * start + fraction * stop