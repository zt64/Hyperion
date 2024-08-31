package dev.zt64.hyperion.ui.component.setting

import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun Setting(
    text: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    icon: ImageVector? = null,
    trailingContent: @Composable (() -> Unit)? = null
) {
    ListItem(
        modifier = modifier,
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
        trailingContent = trailingContent
    )
}