package dev.zt64.hyperion.ui.screen.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.Swipe
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.resources.MR
import dev.zt64.hyperion.ui.component.setting.SliderSetting
import dev.zt64.hyperion.ui.component.setting.SwitchSetting
import dev.zt64.hyperion.ui.model.SettingsScreenModel
import kotlin.math.roundToInt

class GesturesScreen : Screen {
    @Composable
    override fun Content() {
        val model: SettingsScreenModel = koinScreenModel()
        val preferences = model.preferences

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

        AnimatedVisibility(
            visible = preferences.doubleTapToSeek,
            enter = expandVertically(expandFrom = Alignment.Top),
            exit = shrinkVertically(shrinkTowards = Alignment.Top)
        ) {
            SliderSetting(
                text = stringResource(MR.strings.double_tap_to_seek_amount),
                value = preferences.doubleTapToSeekDuration.toFloat(),
                valueRange = 0f..60f,
                steps = 60,
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
}