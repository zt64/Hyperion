package com.hyperion.ui.component.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material3.ListItem
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.reflect.KMutableProperty0

@Composable
fun SliderSetting(
    modifier: Modifier = Modifier,
    preference: KMutableProperty0<Float>,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int = 10,
    text: String,
    label: @Composable ((Float) -> Unit) = {
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
    ListItem(
        modifier = modifier.systemGestureExclusion(),
        headlineContent = { Text(text) },
        supportingContent = {
            var sliderValue by rememberSaveable { mutableFloatStateOf(value) }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Slider(
                    modifier = Modifier.weight(1f),
                    value = sliderValue,
                    valueRange = valueRange,
                    steps = steps,
                    onValueChange = { sliderValue = it },
                    onValueChangeFinished = { onValueChangeFinished(sliderValue) }
                )

                label(sliderValue)
            }
        }
    )
}