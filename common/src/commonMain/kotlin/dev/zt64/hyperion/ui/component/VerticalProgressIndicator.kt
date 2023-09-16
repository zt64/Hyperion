package dev.zt64.hyperion.ui.component

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import dev.zt64.hyperion.ui.tooling.HyperionPreview
import kotlin.math.abs

private val LinearIndicatorWidth = 4.0.dp
private val LinearIndicatorHeight = 240.dp

@Composable
fun VerticalProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = ProgressIndicatorDefaults.linearColor,
    trackColor: Color = ProgressIndicatorDefaults.linearTrackColor,
    strokeCap: StrokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
) {
    val coercedProgress = progress.coerceIn(0f, 1f)

    Canvas(
        modifier = modifier
            .progressSemantics(coercedProgress)
            .size(LinearIndicatorWidth, LinearIndicatorHeight)
    ) {
        val strokeWidth = size.width
        drawLinearIndicator(0f, 1f, trackColor, strokeWidth, strokeCap)
        drawLinearIndicator(0f, coercedProgress, color, strokeWidth, strokeCap)
    }
}

private fun DrawScope.drawLinearIndicator(
    startFraction: Float,
    endFraction: Float,
    color: Color,
    strokeWidth: Float,
    strokeCap: StrokeCap,
) {
    val width = size.width
    val height = size.height
    val xOffset = width / 2

    val barStart = (startFraction * -height) + height
    val barEnd = (endFraction * -height) + height

    if (strokeCap == StrokeCap.Butt || width > height) {
        // Progress line
        drawLine(color, Offset(xOffset, barStart), Offset(xOffset, barEnd), strokeWidth)
    } else {
        val strokeCapOffset = strokeWidth / 2
        val coerceRange = strokeCapOffset..(height - strokeCapOffset)
        val adjustedBarStart = barStart.coerceIn(coerceRange)
        val adjustedBarEnd = barEnd.coerceIn(coerceRange)

        if (abs(endFraction - startFraction) > 0) {
            drawLine(
                color = color,
                start = Offset(xOffset, adjustedBarStart),
                end = Offset(xOffset, adjustedBarEnd),
                strokeWidth = strokeWidth,
                cap = strokeCap,
            )
        }
    }
}

@Preview
@Composable
private fun VerticalProgressIndicatorPreview() {
    HyperionPreview {
        VerticalProgressIndicator(progress = 0.25f)
    }
}