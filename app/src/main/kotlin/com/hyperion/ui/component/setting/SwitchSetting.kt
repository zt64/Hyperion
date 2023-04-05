package com.hyperion.ui.component.setting

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun SwitchSetting(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    checked: Boolean,
    icon: ImageVector? = null,
    text: String,
    description: String? = null,
    onCheckedChange: (value: Boolean) -> Unit
) {
    ListItem(
        modifier = modifier.clickable(enabled) {
            onCheckedChange(!checked)
        },
        headlineContent = { Text(text) },
        supportingContent = description?.let { { Text(it) } },
        leadingContent = icon?.let { imageVector ->
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