package com.hyperion.ui.component

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hyperion.R
import com.hyperion.domain.model.DomainSearch

@Composable
fun ChannelCard(
    channel: DomainSearch.Result.Channel,
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
                .heightIn(min = 70.dp)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ChannelThumbnail(
                modifier = Modifier.size(60.dp),
                url = channel.thumbnailUrl
            )

            Column(
                modifier = Modifier
                    .weight(1f, true)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = channel.name,
                    style = MaterialTheme.typography.labelLarge
                )

                if (channel.subscriptionsText != null) {
                    Text(
                        text = channel.subscriptionsText,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                if (channel.videoCountText != null) {
                    Text(
                        text = channel.videoCountText,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Button(onClick = onClickSubscribe) {
                Text(stringResource(R.string.subscribe))
            }
        }
    }
}