package dev.zt64.hyperion.ui.sheet

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.MR
import dev.zt64.hyperion.ui.tooling.HyperionPreview

@Composable
fun DownloadSheet(onDismissRequest: () -> Unit) {
    var showQualityDialog by rememberSaveable { mutableStateOf(false) }
    var showFormatDialog by rememberSaveable { mutableStateOf(false) }

    var audioOnly by rememberSaveable { mutableStateOf(false) }
    var downloadSubtitles by rememberSaveable { mutableStateOf(false) }
    var saveThumbnail by rememberSaveable { mutableStateOf(true) }

    if (showQualityDialog) {
        AlertDialog(
            onDismissRequest = { showQualityDialog = false }
        ) {

        }
    }

    if (showFormatDialog) {
        AlertDialog(
            onDismissRequest = { showFormatDialog = false }
        ) {

        }
    }

    ModalBottomSheet(onDismissRequest) {
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
                    onClick = { showQualityDialog = true },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.HighQuality,
                            contentDescription = null
                        )
                    },
                    label = { Text("Resolution") }
                )
                AssistChip(
                    onClick = { showFormatDialog = true },
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

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InputChip(
                    selected = audioOnly,
                    onClick = { audioOnly = !audioOnly },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.AudioFile,
                            contentDescription = null
                        )
                    },
                    label = { Text("Audio only") }
                )
                InputChip(
                    selected = downloadSubtitles,
                    onClick = { downloadSubtitles = !downloadSubtitles },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Subtitles,
                            contentDescription = null
                        )
                    },
                    label = { Text(stringResource(MR.strings.subtitles)) }
                )
                InputChip(
                    selected = saveThumbnail,
                    onClick = { saveThumbnail = !saveThumbnail },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null
                        )
                    },
                    label = { Text(stringResource(MR.strings.thumbnail)) }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
            ) {
                OutlinedButton(
                    onClick = onDismissRequest
                ) {
                    Text(stringResource(MR.strings.cancel))
                }

                Button(
                    onClick = {}
                ) {
                    Text(stringResource(MR.strings.download))
                }
            }
        }
    }
}

@Preview
@Composable
private fun DownloadSheetPreview() {
    HyperionPreview {
        DownloadSheet(onDismissRequest = {})
    }
}