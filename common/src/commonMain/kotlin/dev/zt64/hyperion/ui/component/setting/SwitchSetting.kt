package dev.zt64.hyperion.ui.component.setting

import androidx.compose.foundation.clickable
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import kotlin.reflect.KMutableProperty0

@Suppress("NOTHING_TO_INLINE")
@Composable
internal inline fun SwitchSetting(
    preference: KMutableProperty0<Boolean>,
    text: String,
    subtext: String? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    description: String? = null
) {
    SwitchSetting(
        modifier = modifier,
        enabled = enabled,
        checked = preference.get(),
        icon = icon,
        text = text,
        subtext = subtext,
        description = description,
        onCheckedChange = preference::set
    )
}

@Composable
internal fun SwitchSetting(
    text: String,
    checked: Boolean,
    onCheckedChange: (value: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    subtext: String? = null,
    icon: ImageVector? = null,
    description: String? = null
) {
    Setting(
        modifier = modifier.clickable(enabled) {
            onCheckedChange(!checked)
        },
        text = text,
        description = description,
        icon = icon,
        trailingContent = {
            Switch(
                enabled = enabled,
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    )
}