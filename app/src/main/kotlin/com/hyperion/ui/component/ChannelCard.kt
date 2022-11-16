package com.hyperion.ui.component

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hyperion.R
import com.zt.innertube.domain.model.DomainChannelPartial

@Composable
fun ChannelCard(
    channel: DomainChannelPartial,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onClickSubscribe: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .clip(CardDefaults.elevatedShape)
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ShimmerImage(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(60.dp),
                model = channel.thumbnailUrl!!,
                contentDescription = channel.name!!
            )

            Spacer(Modifier.width(12.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = channel.name!!,
                    style = MaterialTheme.typography.titleMedium
                )

                channel.subscriptionsText?.let { subscriptionsText ->
                    Text(
                        text = subscriptionsText,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                channel.videoCountText?.let { videoCountText ->
                    Text(
                        text = videoCountText,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(Modifier.weight(1f, true))

            Button(onClick = onClickSubscribe) {
                Text(stringResource(R.string.subscribe))
            }
        }
    }
}