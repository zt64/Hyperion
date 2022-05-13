package com.hyperion.ui.components

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.hyperion.R
import com.hyperion.domain.model.DomainVideoPartial

@OptIn(ExperimentalMaterial3Api::class, ExperimentalUnitApi::class)
@Composable
fun VideoCard(
    video: DomainVideoPartial,
    onChannelClick: () -> Unit,
    onClick: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        @SuppressLint("SwitchIntDef")
        when (LocalConfiguration.current.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                Box {
                    AsyncImage(
                        modifier = Modifier.aspectRatio(16f / 9f),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(video.thumbnailUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = stringResource(R.string.thumbnail)
                    )

                    if (video.timestamp != null) {
                        Timestamp(
                            modifier = Modifier.align(Alignment.BottomEnd),
                            text = video.timestamp
                        )
                    }
                }

                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ChannelThumbnail(
                        modifier = Modifier
                            .clickable(onClick = onChannelClick)
                            .size(36.dp),
                        url = video.author!!.avatarUrl!!
                    )

                    Column {
                        Text(
                            text = video.title,
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 2,
                        )

                        Text(
                            modifier = Modifier.padding(top = 4.dp),
                            text = video.subtitle,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
            Configuration.ORIENTATION_LANDSCAPE -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                ) {
                    Box(
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        AsyncImage(
                            modifier = Modifier.fillMaxHeight(),
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(video.thumbnailUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = stringResource(R.string.thumbnail)
                        )

                        if (video.timestamp != null) {
                            Timestamp(
                                modifier = Modifier.align(Alignment.BottomEnd),
                                text = video.timestamp
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .heightIn(min = 70.dp)
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = video.title,
                            style = MaterialTheme.typography.labelMedium
                        )

                        Text(
                            text = video.subtitle,
                            style = MaterialTheme.typography.labelSmall
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ChannelThumbnail(
                                modifier = Modifier
                                    .clickable(onClick = onChannelClick)
                                    .size(28.dp),
                                url = video.author!!.avatarUrl!!
                            )

                            Text(
                                text = video.subtitle,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }
    }
}