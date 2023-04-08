package com.hyperion.ui.component.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material3.ListItem
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SliderSetting(
    modifier: Modifier = Modifier,
    text: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int = 10,
    onValueChange: (value: Float) -> Unit = { },
    onValueChangeFinished: (value: Float) -> Unit,
    label: @Composable ((Float) -> Unit) = {
        Text("%.1f".format(it))
    }
) {
    ListItem(
        modifier = modifier.systemGestureExclusion(),
        headlineContent = { Text(text) },
        supportingContent = {
            var sliderValue by rememberSaveable { mutableStateOf(value) }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Slider(
                    modifier = Modifier.weight(1f),
                    value = sliderValue,
                    valueRange = valueRange,
                    steps = steps,
                    onValueChange = {
                        onValueChange(it)
                        sliderValue = it
                    },
                    onValueChangeFinished = { onValueChangeFinished(sliderValue) }
                )

                label(sliderValue)
            }
        }
    )
}