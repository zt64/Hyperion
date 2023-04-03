package com.hyperion.ui.screen.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.Swipe
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.hyperion.R
import com.hyperion.domain.manager.PreferencesManager
import com.hyperion.ui.component.setting.SliderSetting
import com.hyperion.ui.component.setting.SwitchSetting
import kotlin.math.roundToInt

context(ColumnScope)
@Composable
fun GesturesScreen(preferences: PreferencesManager) {
    SwitchSetting(
        checked = preferences.swipeToSeek,
        text = stringResource(R.string.swipe_to_seek),
        icon = Icons.Default.Swipe,
        onCheckedChange = { preferences.swipeToSeek = it }
    )

    SwitchSetting(
        checked = preferences.doubleTapToSeek,
        text = stringResource(R.string.double_tap_to_seek),
        icon = Icons.Default.TouchApp,
        onCheckedChange = { preferences.doubleTapToSeek = it }
    )

    AnimatedVisibility(visible = preferences.doubleTapToSeek) {
        SliderSetting(
            text = stringResource(R.string.double_tap_to_seek_amount),
            value = preferences.doubleTapToSeekDuration.toFloat(),
            valueRange = 1f..60f,
            steps = 59,
            onValueChangeFinished = { preferences.doubleTapToSeekDuration = it.roundToInt() }
        )
    }

    SwitchSetting(
        checked = preferences.volumeGesture,
        text = stringResource(R.string.volume_gesture),
        icon = Icons.Default.VolumeUp,
        onCheckedChange = { preferences.volumeGesture = it }
    )

    SwitchSetting(
        checked = preferences.brightnessGesture,
        text = stringResource(R.string.brightness_gesture),
        icon = Icons.Default.BrightnessHigh,
        onCheckedChange = { preferences.brightnessGesture = it }
    )
}