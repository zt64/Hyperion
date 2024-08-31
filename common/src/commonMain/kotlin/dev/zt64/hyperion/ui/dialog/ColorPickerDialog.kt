package dev.zt64.hyperion.ui.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.godaddy.android.colorpicker.HsvColor
import com.godaddy.android.colorpicker.harmony.ColorHarmonyMode
import com.godaddy.android.colorpicker.harmony.HarmonyColorPicker
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.resources.MR

/**
 * A dialog that allows the user to pick a color.
 *
 * @param color
 * @param title
 * @param onColorSelected
 * @param onDismissRequest
 */
@Composable
fun ColorPickerDialog(color: Color, title: String, onColorSelected: (Color) -> Unit, onDismissRequest: () -> Unit) {
    var selectedColor by remember {
        mutableStateOf(HsvColor.from(color))
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(title) },
        text = {
            HarmonyColorPicker(
                harmonyMode = ColorHarmonyMode.NONE,
                color = selectedColor,
                onColorChanged = { selectedColor = it }
            )
        },
        confirmButton = {
            FilledTonalButton(
                onClick = {
                    onColorSelected(selectedColor.toColor())
                }
            ) {
                Text("Select")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismissRequest) {
                Text(stringResource(MR.strings.cancel))
            }
        }
    )
}