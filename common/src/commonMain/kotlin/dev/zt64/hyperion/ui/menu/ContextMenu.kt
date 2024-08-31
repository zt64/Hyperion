package dev.zt64.hyperion.ui.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties

/**
 * A context menu that can be used to display a list of actions to the user.
 * On Android, the context menu is a sheet that slides up from the bottom of the screen.
 * On desktop, the context menu is a popup that appears near the mouse cursor.
 *
 * Use this instead of the ContextMenuArea and ContextMenuItem. They are bad and evil.
 * For getting input from the user, use a [dev.zt64.hyperion.ui.form.Form] instead.
 *
 * @param menuItems
 * @param modifier
 * @param offset
 * @param scrollState
 * @param properties
 * @param shape
 * @param containerColor
 * @param tonalElevation
 * @param shadowElevation
 * @param border
 * @param content
 */
@Composable
expect fun ContextMenu(
    menuItems: @Composable ContextMenuScope.() -> Unit,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(0.dp, 0.dp),
    scrollState: ScrollState = rememberScrollState(),
    properties: PopupProperties = PopupProperties(focusable = true),
    shape: Shape = MenuDefaults.shape,
    containerColor: Color = MenuDefaults.containerColor,
    tonalElevation: Dp = MenuDefaults.TonalElevation,
    shadowElevation: Dp = MenuDefaults.ShadowElevation,
    border: BorderStroke? = null,
    content: @Composable () -> Unit
)

interface ContextMenuScope {
    fun close()
}

@Composable
fun ContextMenuScope.ContextMenuItem(
    text: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    colors: MenuItemColors = MenuDefaults.itemColors(),
    contentPadding: PaddingValues = MenuDefaults.DropdownMenuItemContentPadding,
    interactionSource: MutableInteractionSource? = null
) {
    DropdownMenuItem(
        text = text,
        onClick = {
            onClick()
            close()
        },
        modifier = modifier,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        enabled = enabled,
        colors = colors,
        contentPadding = contentPadding,
        interactionSource = interactionSource
    )
}