package dev.zt64.hyperion.ui.sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material3.AssistChip
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.resources.MR
import dev.zt64.hyperion.ui.tooling.HyperionPreview

@Composable
fun DownloadSheet(onDismissRequest: () -> Unit) {
    val uiState = rememberSaveable { UiState() }

    if (uiState.showQualityDialog) {
        BasicAlertDialog(onDismissRequest = uiState::dismissQualityDialog) {
        }
    }

    if (uiState.showFormatDialog) {
        BasicAlertDialog(onDismissRequest = uiState::dismissFormatDialog) {
        }
    }

    ModalBottomSheet(onDismissRequest) {
        SheetContent(
            uiState = uiState,
            onDismissRequest = onDismissRequest,
            onClickDownload = {}
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SheetContent(
    uiState: UiState,
    onDismissRequest: () -> Unit,
    onClickDownload: () -> Unit
) {
    Column(
        modifier = Modifier
            .navigationBarsPadding() // workaround bug
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(MR.strings.general),
            style = MaterialTheme.typography.labelLarge
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AssistChip(
                onClick = uiState::showQualityDialog,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.HighQuality,
                        contentDescription = null
                    )
                },
                label = { Text("Resolution") }
            )

            AssistChip(
                onClick = uiState::showFormatDialog,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Extension,
                        contentDescription = null
                    )
                },
                label = { Text("Format") }
            )
        }

        Text(
            text = "Features",
            style = MaterialTheme.typography.labelLarge
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InputChip(
                selected = uiState.audioOnly,
                onClick = uiState::toggleAudioOnly,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.AudioFile,
                        contentDescription = null
                    )
                },
                label = { Text("Audio only") }
            )

            InputChip(
                selected = uiState.downloadSubtitles,
                onClick = uiState::toggleDownloadSubtitles,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Subtitles,
                        contentDescription = null
                    )
                },
                label = { Text(stringResource(MR.strings.subtitles)) }
            )

            InputChip(
                selected = uiState.saveThumbnail,
                onClick = uiState::toggleSaveThumbnail,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null
                    )
                },
                label = {
                    Text(stringResource(MR.strings.thumbnail))
                }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
        ) {
            OutlinedButton(onClick = onDismissRequest) {
                Text(stringResource(MR.strings.cancel))
            }

            Button(onClick = onClickDownload) {
                Text(stringResource(MR.strings.download))
            }
        }
    }
}

@Preview
@Composable
private fun DownloadSheetPreview() {
    HyperionPreview {
        SheetContent(
            uiState = remember { UiState() },
            onDismissRequest = {},
            onClickDownload = {}
        )
    }
}

@Stable
private class UiState {
    var showQualityDialog by mutableStateOf(false)
        private set
    var showFormatDialog by mutableStateOf(false)
        private set

    var audioOnly by mutableStateOf(false)
        private set
    var downloadSubtitles by mutableStateOf(false)
        private set
    var saveThumbnail by mutableStateOf(true)
        private set

    fun dismissQualityDialog() {
        showQualityDialog = false
    }

    fun dismissFormatDialog() {
        showFormatDialog = false
    }

    fun showQualityDialog() {
        showQualityDialog = true
    }

    fun showFormatDialog() {
        showFormatDialog = true
    }

    fun toggleAudioOnly() {
        audioOnly = !audioOnly
    }

    fun toggleDownloadSubtitles() {
        downloadSubtitles = !downloadSubtitles
    }

    fun toggleSaveThumbnail() {
        saveThumbnail = !saveThumbnail
    }
}