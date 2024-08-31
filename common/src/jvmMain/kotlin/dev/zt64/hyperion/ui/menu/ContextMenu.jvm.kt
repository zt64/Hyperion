package dev.zt64.hyperion.ui.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.contextMenuOpenDetector
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import java.awt.MouseInfo
import java.awt.Toolkit
import java.awt.event.WindowEvent
import java.awt.event.WindowFocusListener
import javax.swing.SwingUtilities

// I honestly don't understand how this works, but it does
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
    var expanded by remember {
        mutableStateOf(false)
    }

    val contextMenuScope = remember {
        object : ContextMenuScope {
            override fun close() {
                expanded = false
            }
        }
    }

    Box(
        modifier = Modifier.contextMenuOpenDetector {
            expanded = true
        }
    ) {
        content()
    }

    if (expanded) {
        val density = LocalDensity.current
        val layoutDirection = LocalLayoutDirection.current

        val usableScreenBounds = remember {
            Toolkit.getDefaultToolkit().screenSize.let {
                IntRect(offset = IntOffset.Zero, size = IntSize(it.width, it.height))
            }
        }
        val state = rememberWindowState(
            size = DpSize(200.dp, Dp.Unspecified)
        )

        LaunchedEffect(state.size) {
            if (state.size == DpSize.Unspecified) return@LaunchedEffect

            state.position = with(density) {
                val origin = calculateBoundsRelativeToUsableScreen(
                    usableScreenBounds = usableScreenBounds,
                    anchorBoundsInScreen = IntRect(
                        offset = getCursorPos(),
                        size = IntSize(1, 1)
                    ),
                    contentSize = state.size.let { (w, h) ->
                        IntSize(w.roundToPx(), h.roundToPx())
                    },
                    popupPositionProvider = AlignmentOffsetPositionProvider(
                        Alignment.TopStart,
                        IntOffset.Zero
                    ),
                    layoutDirection = layoutDirection
                ).translate(usableScreenBounds.topLeft).topLeft

                WindowPosition(
                    x = origin.x.toDp(),
                    y = origin.y.toDp()
                )
            }
        }

        Window(
            state = state,
            visible = state.size.isSpecified,
            undecorated = true,
            resizable = false,
            focusable = true,
            onCloseRequest = { expanded = false },
            onKeyEvent = {
                if (it.isDismissRequest()) {
                    expanded = false
                    true
                } else {
                    false
                }
            }
        ) {
            LaunchedEffect(Unit) {
                val focusListener = object : WindowFocusListener {
                    override fun windowGainedFocus(ev: WindowEvent) = Unit

                    override fun windowLostFocus(ev: WindowEvent) {
                        expanded = false
                    }
                }

                SwingUtilities.invokeLater {
                    window.addWindowFocusListener(focusListener)
                }
            }

            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                tonalElevation = 2.dp
            ) {
                Column {
                    contextMenuScope.menuItems()
                }
            }
        }
    }
}

private class AlignmentOffsetPositionProvider(private val alignment: Alignment, private val offset: IntOffset) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        val anchorPoint = alignment.align(IntSize.Zero, anchorBounds.size, layoutDirection)
        val popupPosition = alignment.align(popupContentSize, windowSize, layoutDirection)
        return IntOffset(
            x = anchorBounds.left + anchorPoint.x + offset.x - popupPosition.x,
            y = anchorBounds.top + anchorPoint.y + offset.y - popupPosition.y
        )
    }
}

private fun KeyEvent.isDismissRequest() = type == KeyEventType.KeyDown && key == Key.Escape
private fun getCursorPos(): IntOffset {
    val mousePosition = MouseInfo.getPointerInfo().location
    return IntOffset(mousePosition.x, mousePosition.y)
}

private fun calculateBoundsRelativeToUsableScreen(
    usableScreenBounds: IntRect,
    anchorBoundsInScreen: IntRect,
    contentSize: IntSize,
    popupPositionProvider: PopupPositionProvider,
    layoutDirection: LayoutDirection
): IntRect {
    // Because PopoverPositionProvider takes a *size* for windowSize (really screen size, in our case),
    // coordinates need to be translated to/from a zero-origin rect before and after, to account for any top/left
    // insets in the usable screen bounds.
    val anchorBoundsRelativeToUsableScreen =
        anchorBoundsInScreen.translate(-usableScreenBounds.topLeft)

    val boundsRelativeToUsableScreen = popupPositionProvider.calculatePosition(
        anchorBounds = anchorBoundsRelativeToUsableScreen,
        windowSize = usableScreenBounds.size,
        layoutDirection = layoutDirection,
        popupContentSize = contentSize
    ).let { IntRect(it, contentSize) }

    return boundsRelativeToUsableScreen
}