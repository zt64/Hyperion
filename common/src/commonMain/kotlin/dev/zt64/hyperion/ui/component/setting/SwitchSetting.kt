package dev.zt64.hyperion.ui.component.setting

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import kotlin.reflect.KMutableProperty0

@Composable
fun SwitchSetting(
    preference: KMutableProperty0<Boolean>,
    text: String,
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
    icon: ImageVector? = null,
    description: String? = null
) {
    ListItem(
        modifier =
            modifier.clickable(enabled) {
                onCheckedChange(!checked)
            },
        headlineContent = { Text(text) },
        supportingContent = description?.let { { Text(it) } },
        leadingContent =
            icon?.let { imageVector ->
                {
                    Icon(
                        imageVector = imageVector,
                        contentDescription = text
                    )
                }
            },
        trailingContent = {
            Switch(
                enabled = enabled,
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    )
}