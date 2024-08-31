@file:Suppress("INVISIBLE_REFERENCE")

package dev.zt64.hyperion.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderDefaults.colors
import androidx.compose.material3.SliderState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import dev.zt64.hyperion.api.domain.model.DomainChapter
import dev.zt64.hyperion.domain.model.SponsorBlockCategory
import dev.zt64.hyperion.ui.tooling.HyperionPreview
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

private val TrackHeight = 4.dp

@Stable
data class Segment(val category: SponsorBlockCategory, val range: ClosedFloatingPointRange<Float>)

@Composable
@ExperimentalMaterial3Api
fun SeekBar(
    duration: Duration,
    progress: Duration,
    buffered: Duration,
    onSeek: (Duration) -> Unit,
    modifier: Modifier = Modifier,
    segments: ImmutableList<Segment> = persistentListOf(),
    chapters: ImmutableList<DomainChapter> = persistentListOf(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onSeekFinished: (() -> Unit)? = null,
    colors: SliderColors = SliderDefaults.colors()
) {
    Seekbar(
        duration = duration.inWholeMilliseconds.toFloat(),
        progress = progress.inWholeMilliseconds.toFloat(),
        buffered = buffered.inWholeMilliseconds.toFloat(),
        onSeek = { onSeek(it.toDouble().milliseconds) },
        modifier = modifier,
        segments = segments,
        chapters = chapters,
        interactionSource = interactionSource,
        onSeekFinished = onSeekFinished,
        colors = colors
    )
}

/**
 * A seekbar that shows the progress of a video and allows the user to seek to a different position.
 *
 * @param duration The total duration of the video. (milliseconds)
 * @param progress The current progress of the video. (milliseconds)
 * @param buffered The amount of the video that has been buffered. (milliseconds)
 * @param onSeek A callback that is called when the user seeks to a different position.
 * @param modifier The modifier to apply to the seekbar.
 * @param onSeekFinished A callback that is called when the user has finished seeking.
 * @param segments A list of segments that should be drawn on the seekbar.
 * @param chapters A list of chapters that should be drawn on the seekbar.
 * @param interactionSource The interaction source to use for the seekbar.
 * @param colors The colors to use for the seekbar.
 */
@Composable
fun Seekbar(
    duration: Float,
    progress: Float,
    buffered: Float,
    onSeek: (Float) -> Unit,
    modifier: Modifier = Modifier,
    onSeekFinished: (() -> Unit)? = null,
    segments: ImmutableList<Segment> = persistentListOf(),
    chapters: ImmutableList<DomainChapter> = persistentListOf(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: SliderColors = SliderDefaults.colors()
) {
    Slider(
        value = progress,
        onValueChange = { onSeek(progress) },
        onValueChangeFinished = onSeekFinished,
        interactionSource = interactionSource,
        track = { state ->
            Track(
                segments = segments,
                chapters = chapters,
                sliderState = state,
                buffered = buffered,
                colors = colors
            )
        },
        modifier = modifier.requiredHeight(4.dp),
        thumb = { Thumb(interactionSource, colors = colors) },
        valueRange = 0f..duration
    )
}

@Composable
internal fun Thumb(
    interactionSource: MutableInteractionSource,
    modifier: Modifier = Modifier,
    colors: SliderColors = colors(),
    enabled: Boolean = true,
    thumbSize: DpSize = DpSize(4.dp, 4.dp)
) {
    val isDragged by interactionSource.collectIsDraggedAsState()
    val visible = remember { MutableTransitionState(false) }

    visible.targetState = isDragged

    if (visible.currentState || visible.targetState || !visible.isIdle) {
        Popup(
            alignment = Alignment.BottomCenter,
            offset = IntOffset(0, -80)
        ) {
            AnimatedVisibility(
                visibleState = visible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    modifier = Modifier,
                    shape = MaterialTheme.shapes.medium,
                    tonalElevation = 3.dp
                ) {
                    Box(
                        modifier = Modifier.padding(8.dp)
                    ) {
                    }
                }
            }
        }
    }

    val interactions = remember { mutableStateListOf<Interaction>() }
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> interactions += interaction
                is PressInteraction.Release -> interactions -= interaction.press
                is PressInteraction.Cancel -> interactions -= interaction.press
                is DragInteraction.Start -> interactions += interaction
                is DragInteraction.Stop -> interactions -= interaction.start
                is DragInteraction.Cancel -> interactions -= interaction.start
            }
        }
    }

    val size = if (interactions.isNotEmpty()) {
        thumbSize.copy(width = thumbSize.width / 2)
    } else {
        thumbSize
    }

    @Suppress("INVISIBLE_MEMBER")
    Spacer(
        modifier = modifier
            .size(size)
            .hoverable(interactionSource = interactionSource)
            .background(colors.thumbColor(enabled), CircleShape)
    )
}

@Suppress("INVISIBLE_MEMBER")
@Composable
internal fun Track(
    segments: ImmutableList<Segment>,
    chapters: ImmutableList<DomainChapter>,
    buffered: Float,
    sliderState: SliderState,
    modifier: Modifier = Modifier,
    colors: SliderColors = SliderDefaults.colors(),
    enabled: Boolean = true
) {
    val inactiveTrackColor = colors.trackColor(enabled, active = false)
    val activeTrackColor = colors.trackColor(enabled, active = true)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(TrackHeight)
            .rotate(if (LocalLayoutDirection.current == LayoutDirection.Rtl) 180f else 0f)
    ) {
        val isRtl = layoutDirection == LayoutDirection.Rtl
        val sliderLeft = Offset(0f, center.y)
        val sliderRight = Offset(size.width, center.y)
        val sliderStart = if (isRtl) sliderRight else sliderLeft
        val sliderEnd = if (isRtl) sliderLeft else sliderRight
        val trackStrokeWidth = TrackHeight.toPx()

        val bufferedEnd = Offset(
            x = sliderStart.x + (sliderEnd.x - sliderStart.x) * buffered,
            y = center.y
        )

        // Draw the bottom track
        drawLine(
            color = inactiveTrackColor,
            start = sliderStart,
            end = sliderEnd,
            strokeWidth = trackStrokeWidth,
            cap = StrokeCap.Round
        )

        // Draw the buffered track
        drawLine(
            color = activeTrackColor.copy(alpha = 0.5f),
            start = sliderStart,
            end = Offset(
                x = calcFraction(0f, sliderState.valueRange.endInclusive, buffered) * size.width,
                y = center.y
            ),
            strokeWidth = trackStrokeWidth,
            cap = StrokeCap.Round
        )

        // // Draw the unbuffered track
        // drawLine(
        //     color = inactiveTrackColor.copy(alpha = 0.5f),
        //     start = bufferedEnd,
        //     end = sliderEnd,
        //     strokeWidth = trackStrokeWidth,
        //     cap = StrokeCap.Round
        // )

        // Draw the track up to the current value
        drawTrack(
            activeRangeStart = 0f,
            activeRangeEnd = sliderState.coercedValueAsFraction,
            inactiveTrackColor = inactiveTrackColor,
            activeTrackColor = activeTrackColor
        )

        segments.sortedBy { it.range.start }.forEach { segment ->
            val segmentStart = Offset(
                x = sliderStart.x + (sliderEnd.x - sliderStart.x) * segment.range.start,
                y = center.y
            )

            val segmentEnd = Offset(
                x = sliderStart.x + (sliderEnd.x - sliderStart.x) * segment.range.endInclusive,
                y = center.y
            )

            drawLine(
                color = segment.category.defaultColor,
                start = segmentStart,
                end = segmentEnd,
                strokeWidth = trackStrokeWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

context(DrawScope)
@Suppress("INVISIBLE_MEMBER")
private fun drawTrack(
    activeRangeStart: Float,
    activeRangeEnd: Float,
    inactiveTrackColor: Color,
    activeTrackColor: Color
) {
    val isRtl = layoutDirection == LayoutDirection.Rtl
    val sliderLeft = Offset(0f, center.y)
    val sliderRight = Offset(size.width, center.y)
    val sliderStart = if (isRtl) sliderRight else sliderLeft
    val sliderEnd = if (isRtl) sliderLeft else sliderRight
    val trackStrokeWidth = TrackHeight.toPx()

    // drawLine(
    //     color = inactiveTrackColor,
    //     start = sliderStart,
    //     end = sliderEnd,
    //     strokeWidth = trackStrokeWidth,
    //     cap = StrokeCap.Round
    // )
    val sliderValueEnd = Offset(
        sliderStart.x + (sliderEnd.x - sliderStart.x) * activeRangeEnd,
        center.y
    )

    val sliderValueStart = Offset(
        sliderStart.x + (sliderEnd.x - sliderStart.x) * activeRangeStart,
        center.y
    )

    drawLine(
        color = activeTrackColor,
        start = sliderValueStart,
        end = sliderValueEnd,
        strokeWidth = trackStrokeWidth,
        cap = StrokeCap.Round
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun SeekBarPreview() {
    HyperionPreview {
        var progress by remember { mutableStateOf(30.minutes) }

        Surface(
            modifier = Modifier.padding(16.dp)
        ) {
            SeekBar(
                duration = 1.hours,
                progress = progress,
                buffered = 45.minutes,
                segments = persistentListOf(
                    Segment(SponsorBlockCategory.INTRO, 0.0f..0.1f),
                    Segment(SponsorBlockCategory.SPONSOR, 0.7f..0.75f),
                    Segment(SponsorBlockCategory.OUTRO, 0.8f..1.0f)
                ),
                chapters = persistentListOf(
                    DomainChapter("Chapter 1", "", 20.seconds)
                ),
                onSeek = { progress = it }
            )
        }
    }
}

private fun calcFraction(
    a: Float,
    b: Float,
    pos: Float
) = (if (b - a == 0f) 0f else (pos - a) / (b - a)).coerceIn(0f, 1f)

object SeekBarDefaults