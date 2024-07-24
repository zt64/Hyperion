package dev.zt64.hyperion.ui.screen.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.Swipe
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.domain.manager.PreferencesManager
import dev.zt64.hyperion.resources.MR
import dev.zt64.hyperion.ui.component.setting.SliderSetting
import dev.zt64.hyperion.ui.component.setting.SwitchSetting
import kotlin.math.roundToInt

context(ColumnScope)
@Composable
fun GesturesScreen(preferences: PreferencesManager) {
    SwitchSetting(
        preference = preferences::swipeToSeek,
        text = stringResource(MR.strings.swipe_to_seek),
        icon = Icons.Default.Swipe
    )

    SwitchSetting(
        preference = preferences::doubleTapToSeek,
        text = stringResource(MR.strings.double_tap_to_seek),
        icon = Icons.Default.TouchApp
    )

    AnimatedVisibility(visible = preferences.doubleTapToSeek) {
        SliderSetting(
            text = stringResource(MR.strings.double_tap_to_seek_amount),
            value = preferences.doubleTapToSeekDuration.toFloat(),
            valueRange = 1f..60f,
            steps = 59,
            onValueChangeFinished = { preferences.doubleTapToSeekDuration = it.roundToInt() }
        )
    }

    SwitchSetting(
        preference = preferences::volumeGesture,
        text = stringResource(MR.strings.volume_gesture),
        icon = Icons.AutoMirrored.Default.VolumeUp
    )

    SwitchSetting(
        preference = preferences::brightnessGesture,
        text = stringResource(MR.strings.brightness_gesture),
        icon = Icons.Default.BrightnessHigh
    )
}