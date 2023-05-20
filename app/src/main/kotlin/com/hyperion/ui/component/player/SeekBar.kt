@file:Suppress(
    "INVISIBLE_SETTER",
    "INVISIBLE_REFERENCE",
)

package com.hyperion.ui.component.player

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.material3.tokens.SliderTokens
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.zt.innertube.domain.model.DomainChapter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

enum class Category {
    SPONSOR,
    SELF_PROMO,
    INTERACTION,
    INTRO,
    OUTRO,
    PREVIEW,
    MUSIC_OFF_TOPIC,
    FILLER
}

@Stable
data class Segment(
    val category: Category,
    val range: ClosedFloatingPointRange<Float>,
)

@Composable
@ExperimentalMaterial3Api
fun SeekBar(
    modifier: Modifier = Modifier,
    duration: Duration,
    progress: Duration,
    buffered: Duration,
    segments: ImmutableList<Segment> = persistentListOf(),
    chapters: ImmutableList<DomainChapter> = persistentListOf(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onSeek: (Duration) -> Unit,
    onSeekFinished: (() -> Unit)? = null,
    colors: SliderColors = SliderDefaults.colors(),
) {
    Slider(
        modifier = modifier,
        value = (progress / duration).toFloat(),
        onValueChange = { onSeek((duration.inWholeMilliseconds * it).toInt().milliseconds) },
        onValueChangeFinished = onSeekFinished,
        interactionSource = interactionSource,
        thumb = {
            Thumb(
                interactionSource = interactionSource,
                colors = colors
            )
        },
        track = { sliderPositions ->
            Track(
                segments = segments,
                sliderPositions = sliderPositions,
                buffered = (buffered / duration).toFloat(),
                colors = colors
            )
        }
    )
}

@Preview
@Composable
private fun SeekBarPreview() {
    var progress by remember { mutableStateOf(30.minutes) }

    SeekBar(
        duration = 1.hours,
        progress = progress,
        buffered = 60.minutes,
        segments = persistentListOf(
            Segment(Category.INTRO, 0.0f..0.1f),
            Segment(Category.SPONSOR, 0.7f..0.75f),
            Segment(Category.OUTRO, 0.8f..1.0f),
        ),
        onSeek = { progress = it }
    )
}

@Suppress("INVISIBLE_MEMBER")
@Composable
private fun Thumb(
    interactionSource: MutableInteractionSource,
    modifier: Modifier = Modifier,
    colors: SliderColors = SliderDefaults.colors(),
    enabled: Boolean = true,
    thumbSize: DpSize = DpSize(SliderTokens.HandleWidth, SliderTokens.HandleHeight)
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
    val shape = SliderTokens.HandleShape.toShape()

    Spacer(
        modifier
            .size(thumbSize)
            .indication(
                interactionSource = interactionSource,
                indication = rememberRipple(
                    bounded = false,
                    radius = SliderTokens.StateLayerSize / 2
                )
            )
            .hoverable(interactionSource = interactionSource)
            .shadow(if (enabled) elevation else 0.dp, shape, clip = false)
            .background(colors.thumbColor(enabled).value, shape)
    )
}

@Suppress("INVISIBLE_MEMBER")
@Composable
private fun Track(
    segments: ImmutableList<Segment>,
    buffered: Float,
    sliderPositions: SliderPositions,
    modifier: Modifier = Modifier,
    colors: SliderColors = SliderDefaults.colors(),
    enabled: Boolean = true,
) {
    val inactiveTrackColor by colors.trackColor(enabled, active = false)
    val activeTrackColor by colors.trackColor(enabled, active = true)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(TrackHeight)
    ) {
        val isRtl = layoutDirection == LayoutDirection.Rtl
        val sliderLeft = Offset(0f, center.y)
        val sliderRight = Offset(size.width, center.y)
        val sliderStart = if (isRtl) sliderRight else sliderLeft
        val sliderEnd = if (isRtl) sliderLeft else sliderRight
        val trackStrokeWidth = TrackHeight.toPx()

        val bufferedEnd = Offset(
            x = sliderStart.x +
                (sliderEnd.x - sliderStart.x) * buffered,
            y = center.y
        )
        drawLine(
            color = inactiveTrackColor,
            start = sliderStart,
            end = bufferedEnd,
            strokeWidth = trackStrokeWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = inactiveTrackColor.copy(alpha = 0.5f),
            start = sliderStart,
            end = sliderEnd,
            strokeWidth = trackStrokeWidth,
            cap = StrokeCap.Round
        )

        val sliderValueEnd = Offset(
            x = sliderStart.x +
                (sliderEnd.x - sliderStart.x) * sliderPositions.activeRange.endInclusive,
            y = center.y
        )

        val sliderValueStart = Offset(
            x = sliderStart.x +
                (sliderEnd.x - sliderStart.x) * sliderPositions.activeRange.start,
            y = center.y
        )

        drawLine(
            color = activeTrackColor,
            start = sliderValueStart,
            end = sliderValueEnd,
            strokeWidth = trackStrokeWidth,
            cap = StrokeCap.Round
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
                color = when (segment.category) {
                    Category.INTRO -> Color(0x7800FFFF)
                    Category.OUTRO -> Color(0x780202ED)
                    Category.PREVIEW -> Color(0x78008FD6)
                    Category.SELF_PROMO -> Color(0x78FFFF00)
                    Category.SPONSOR -> Color(0x7800D400)
                    Category.INTERACTION -> Color(0x78CC00FF)
                    Category.MUSIC_OFF_TOPIC -> Color(0x78FF9900)
                    Category.FILLER -> Color(0x787300FF)
                },
                start = segmentStart,
                end = segmentEnd,
                strokeWidth = trackStrokeWidth,
                cap = StrokeCap.Round
            )
        }
    }
}