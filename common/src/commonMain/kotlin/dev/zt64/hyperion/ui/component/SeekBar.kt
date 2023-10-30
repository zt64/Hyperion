package dev.zt64.hyperion.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import dev.zt64.hyperion.ui.tooling.HyperionPreview
import dev.zt64.innertube.domain.model.DomainChapter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

@Stable
enum class SkipOption {
    DISABLE,
    SHOW,
    MANUAL,
    AUTO
}

enum class SponsorBlockCategory(
    val description: String,
    val color: Color
) {
    SPONSOR(
        description = "Paid promotion, paid referrals and direct advertisements. Not for " +
                "self-promotion or free shoutouts to causes/creators/websites/products they like.",
        color = Color(0x7800D400)
    ) {
        override fun toString() = "Sponsor"
    },
    SELF_PROMO(
        description = "Similar to \"sponsor\" except for unpaid or self promotion. This includes " +
                "sections about merchandise, donations, or information about who they collaborated with.",
        color = Color(0x78FFFF00)
    ) {
        override fun toString() = "Unpaid/Self Promotion"
    },
    INTERACTION(
        description = "Asking for likes, comments, or subscribers. This includes asking for " +
                "engagement in the video, comments, or social media.",
        color = Color(0x78CC00FF)
    ) {
        override fun toString() = "Interaction Reminder"
    },
    HIGHLIGHT(
        description = "The part of the video that most people are looking for. Similar to " +
                "\"Video starts at x\" comments.",
        color = Color(0x78FF0000)
    ) {
        override fun toString() = "Highlight"
    },
    INTRO(
        description = "The introduction to the video. This includes the intro animation, intro " +
                "music, and the creator introducing themselves.",
        color = Color(0x7800FFFF)
    ) {
        override fun toString() = "Intro"
    },
    OUTRO(
        description = "Credits or when the YouTube endcards appear. Not for conclusions with information.",
        color = Color(0x780202ED)
    ) {
        override fun toString() = "Endcards/Credits"
    },
    FILLER(
        description = "Tangential scenes added only for filler or humor that are not required to " +
                "understand the main content of the video.",
        color = Color(0x787300FF)
    ) {
        override fun toString() = "Filler Tangent/Jokes"
    },
    PREVIEW(
        description = "Collection of clips that show what is coming up in in this video or other " +
                "videos in a series where all information is repeated later in the video.",
        color = Color(0x78008FD6)
    ) {
        override fun toString() = "Preview/Recap"
    },
    MUSIC_OFF_TOPIC(
        description = "Only for use in music videos. This only should be used for sections of music " +
                "videos that aren't already covered by another category.",
        color = Color(0x78FF9900)
    ) {
        override fun toString() = "Music: Non-Music Section"
    }
}

@Stable
data class Segment(
    val category: SponsorBlockCategory,
    val range: ClosedFloatingPointRange<Float>,
)

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
    Slider(
        modifier = modifier,
        value = (progress / duration).toFloat(),
        onValueChange = { onSeek((duration.inWholeMilliseconds * it).toInt().milliseconds) },
        onValueChangeFinished = onSeekFinished,
        interactionSource = interactionSource,
        thumb = { Thumb(colors, interactionSource) },
        track = { state ->
            Track(
                segments = segments,
                sliderState = state,
                buffered = (buffered / duration).toFloat(),
                colors = colors
            )
        }
    )
}

@Composable
internal fun Thumb(
    colors: SliderColors,
    interactionSource: MutableInteractionSource
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

    SliderDefaults.Thumb(
        interactionSource = interactionSource,
        colors = colors
    )
}

@Suppress("INVISIBLE_MEMBER")
@Composable
internal fun Track(
    segments: ImmutableList<Segment>,
    buffered: Float,
    sliderState: SliderState,
    modifier: Modifier = Modifier,
    colors: SliderColors = SliderDefaults.colors(),
    enabled: Boolean = true,
) {
    val inactiveTrackColor = colors.commonTrackColor(enabled, active = false)
    val activeTrackColor = colors.commonTrackColor(enabled, active = true)

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
            x = sliderStart.x + (sliderEnd.x - sliderStart.x) * buffered,
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
            start = bufferedEnd,
            end = sliderEnd,
            strokeWidth = trackStrokeWidth,
            cap = StrokeCap.Round
        )

        drawTrack(
            activeRangeStart = 0f,
            activeRangeEnd = sliderState.activeRangeEnd,
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
                color = segment.category.color,
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

    drawLine(
        color = inactiveTrackColor,
        start = sliderStart,
        end = sliderEnd,
        strokeWidth = trackStrokeWidth,
        cap = StrokeCap.Round
    )
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

        SeekBar(
            duration = 1.hours,
            progress = progress,
            buffered = 60.minutes,
            segments = persistentListOf(
                Segment(SponsorBlockCategory.INTRO, 0.0f..0.1f),
                Segment(SponsorBlockCategory.SPONSOR, 0.7f..0.75f),
                Segment(SponsorBlockCategory.OUTRO, 0.8f..1.0f),
            ),
            onSeek = { progress = it }
        )
    }
}