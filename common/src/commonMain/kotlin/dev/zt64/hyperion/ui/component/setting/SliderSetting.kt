package dev.zt64.hyperion.ui.component.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.reflect.KMutableProperty0

@Suppress("NOTHING_TO_INLINE")
@Composable
internal inline fun SliderSetting(
    preference: KMutableProperty0<Float>,
    valueRange: ClosedFloatingPointRange<Float>,
    text: String,
    modifier: Modifier = Modifier,
    steps: Int = 10,
    noinline label: @Composable ((Float) -> Unit) = {
        Text("%.1f".format(it))
    }
) {
    SliderSetting(
        modifier = modifier,
        value = preference.get(),
        valueRange = valueRange,
        steps = steps,
        onValueChangeFinished = preference::set,
        text = text,
        label = label
    )
}

@Composable
fun SliderSetting(
    text: String,
    value: Float,
    onValueChangeFinished: (value: Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 10,
    label: @Composable ((Float) -> Unit) = {
        Text("%.1f".format(it))
    }
) {
    Column {
        var sliderValue by rememberSaveable { mutableFloatStateOf(value) }

        Setting(
            modifier = modifier,
            text = text,
            icon = Icons.Default.Timer,
            trailingContent = {
                label(sliderValue)
            }
        )

        Slider(
            modifier = Modifier.padding(horizontal = 24.dp),
            value = sliderValue,
            valueRange = valueRange,
            steps = steps,
            onValueChange = { sliderValue = it },
            onValueChangeFinished = { onValueChangeFinished(sliderValue) }
        )
    }
}