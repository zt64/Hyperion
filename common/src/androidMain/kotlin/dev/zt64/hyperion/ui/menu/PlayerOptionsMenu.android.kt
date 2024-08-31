package dev.zt64.hyperion.ui.menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.ClosedCaptionOff
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.TrackGroup
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.trackselection.MappingTrackSelector
import cafe.adriel.voyager.koin.koinScreenModel
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.resources.MR
import dev.zt64.hyperion.ui.model.PlayerScreenModel
import dev.zt64.hyperion.ui.screen.PlayerScreen

@Composable
internal actual fun PlayerScreen.PlayerOptionsMenuContent(onDismissRequest: () -> Unit) {
    val model: PlayerScreenModel = koinScreenModel()

    Column(
        modifier = Modifier
            .navigationBarsPadding() // workaround bug
            .padding(12.dp)
    ) {
        ListItem(
            modifier = Modifier.clickable { onDismissRequest() },
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(MR.strings.quality)
                )
            },
            headlineContent = {
                Text(stringResource(MR.strings.quality))
            },
            supportingContent = {
                Text(model.videoFormat!!.qualityLabel)
            }
        )

        ListItem(
            modifier = Modifier.clickable(onClick = model::toggleCaptions),
            leadingContent = {
                Icon(
                    imageVector = if (model.showCaptions) {
                        Icons.Default.ClosedCaption
                    } else {
                        Icons.Default.ClosedCaptionOff
                    },
                    contentDescription = stringResource(MR.strings.captions)
                )
            },
            headlineContent = {
                Text(stringResource(MR.strings.captions))
            }
        )

        ListItem(
            modifier = Modifier.clickable(onClick = model::toggleLoop),
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.Loop,
                    contentDescription = stringResource(MR.strings.loop)
                )
            },
            headlineContent = {
                Text(stringResource(MR.strings.loop))
            },
            supportingContent = {
                Text(
                    stringResource(
                        if (model.repeatMode == Player.REPEAT_MODE_OFF) {
                            MR.strings.off
                        } else {
                            MR.strings.on
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
                    contentDescription = stringResource(MR.strings.playback_speed)
                )
            },
            headlineContent = {
                Text(stringResource(MR.strings.playback_speed))
            },
            supportingContent = {
                Text("1.0x")
            }
        )
    }
}

@UnstableApi
private fun DefaultTrackSelector.generateQualityList(): List<Pair<String, TrackSelectionOverride>> {
    val renderTrack = currentMappedTrackInfo

    return buildList {
        repeat(renderTrack?.rendererCount ?: 0) { rendererIndex ->
            if (renderTrack?.isSupportedFormat(rendererIndex) != true) return@repeat
            if (renderTrack.getRendererType(rendererIndex) != C.TRACK_TYPE_VIDEO) return@repeat

            val trackGroups = renderTrack.getTrackGroups(rendererIndex)

            repeat(trackGroups.length) { groupIndex ->
                val group = trackGroups[groupIndex]

                addAll(group.generateThing(renderTrack, rendererIndex, groupIndex))
            }
        }
    }
}

@UnstableApi
private fun MappingTrackSelector.MappedTrackInfo.isSupportedFormat(rendererIndex: Int): Boolean {
    if (getTrackGroups(rendererIndex).isEmpty) return false

    return getRendererType(rendererIndex) == C.TRACK_TYPE_VIDEO
}

@UnstableApi
private fun TrackGroup.generateThing(renderTrack: MappingTrackSelector.MappedTrackInfo, rendererIndex: Int, groupIndex: Int) = buildList {
    repeat(length) { trackIndex ->
        if (
            renderTrack.getTrackSupport(
                // rendererIndex =
                rendererIndex,
                // groupIndex =
                groupIndex,
                // trackIndex =
                trackIndex
            ) != C.FORMAT_HANDLED
        ) {
            return@repeat
        }

        val format = getFormat(trackIndex)

        val trackName = "${format.width} x ${format.height}"
        val trackBuilder = TrackSelectionOverride(this@generateThing, listOf(trackIndex))

        add(trackName to trackBuilder)
    }
}