package dev.zt64.hyperion.ui.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.window.PopupProperties

@Composable
actual fun ContextMenu(
    menuItems: @Composable (ContextMenuScope.() -> Unit),
    modifier: Modifier,
    offset: DpOffset,
    scrollState: ScrollState,
    properties: PopupProperties,
    shape: Shape,
    containerColor: Color,
    tonalElevation: Dp,
    shadowElevation: Dp,
    border: BorderStroke?,
    content: @Composable () -> Unit
) {
    var expanded by rememberSaveable {
        mutableStateOf(true)
    }

    if (expanded) {
        ModalBottomSheet(
            onDismissRequest = { expanded = false }
        ) {
            val scope = object : ContextMenuScope {
                override fun close() {
                    expanded = false
                }
            }

            menuItems(scope)
        }
    }

    Box(
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(
                onLongPress = {
                    expanded = true
                }
            )
        }
    ) {
        content()
    }
}