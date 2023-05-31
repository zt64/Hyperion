package com.hyperion.ui.sheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import com.hyperion.R
import com.hyperion.ui.viewmodel.PlayerViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlayerSheet(
    viewModel: PlayerViewModel = koinViewModel(),
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(onDismissRequest) {
        //        LazyColumn {
        //            items(viewModel.videoFormats) { format ->
        //                ListItem(
        //                    modifier = Modifier.clickable {
        //                        viewModel.selectFormat(format)
        //                    },
        //                    headlineContent = {
        //                        Text(format.qualityLabel)
        //                    },
        //                    supportingContent = {
        //                        Text(format.mimeType)
        //                    }
        //                )
        //            }
        //        }

        Column(
            modifier = Modifier
                .navigationBarsPadding() // workaround bug
                .padding(12.dp),
        ) {
            ListItem(
                modifier = Modifier.clickable {

                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.quality)
                    )
                },
                headlineContent = {
                    Text(stringResource(R.string.quality))
                },
                supportingContent = {
                    Text(viewModel.videoFormat!!.qualityLabel)
                }
            )

            ListItem(
                modifier = Modifier.clickable(onClick = viewModel::toggleCaptions),
                leadingContent = {
                    Icon(
                        imageVector = if (viewModel.showCaptions) {
                            Icons.Default.ClosedCaption
                        } else {
                            Icons.Default.ClosedCaptionOff
                        },
                        contentDescription = stringResource(R.string.captions)
                    )
                },
                headlineContent = {
                    Text(stringResource(R.string.captions))
                }
            )

            ListItem(
                modifier = Modifier.clickable(onClick = viewModel::toggleLoop),
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Loop,
                        contentDescription = stringResource(R.string.loop)
                    )
                },
                headlineContent = {
                    Text(stringResource(R.string.loop))
                },
                supportingContent = {
                    Text(
                        stringResource(
                            if (viewModel.repeatMode == Player.REPEAT_MODE_OFF) {
                                R.string.off
                            } else {
                                R.string.on
                            }
                        )
                    )
                }
            )

            ListItem(
                modifier = Modifier.clickable {

                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = stringResource(R.string.playback_speed)
                    )
                },
                headlineContent = {
                    Text(stringResource(R.string.playback_speed))
                },
                supportingContent = {
                    Text("1.0x")
                }
            )
        }
    }
}