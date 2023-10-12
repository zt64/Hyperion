package dev.zt64.hyperion.ui.component

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.MR
import dev.zt64.hyperion.ui.tooling.HyperionPreview
import dev.zt64.innertube.domain.model.DomainChannelPartial

@Composable
fun ChannelCard(
    channel: DomainChannelPartial,
    isLoggedIn: Boolean,
    onClick: () -> Unit,
    onClickSubscribe: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        onLongClick = onLongClick
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
                url = channel.avatarUrl!!,
                contentDescription = channel.name!!
            )

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f, true),
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

            Button(
                enabled = isLoggedIn,
                onClick = onClickSubscribe
            ) {
                Text(stringResource(MR.strings.subscribe))
            }
        }
    }
}

@Preview
@Composable
private fun ChannelCardPreview() {
    HyperionPreview {
        ChannelCard(
            channel = DomainChannelPartial(
                id = "",
                name = "Linus Tech Tips",
                avatarUrl = "",
                subscriptionsText = "10.5M subscribers",
                videoCountText = "4,000 videos"
            ),
            isLoggedIn = true,
            onClick = {},
            onLongClick = {},
            onClickSubscribe = {}
        )
    }
}