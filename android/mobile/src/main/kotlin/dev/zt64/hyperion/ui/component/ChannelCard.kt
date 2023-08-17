package dev.zt64.hyperion.ui.component

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import dev.olshevski.navigation.reimagined.navigate
import dev.zt64.hyperion.LocalNavController
import dev.zt64.hyperion.MR
import dev.zt64.hyperion.domain.manager.AccountManager
import dev.zt64.hyperion.ui.navigation.AppDestination
import dev.zt64.innertube.domain.model.DomainChannelPartial
import org.koin.compose.koinInject

@Composable
fun ChannelCard(
    channel: DomainChannelPartial,
    onLongClick: () -> Unit,
    onClickSubscribe: () -> Unit,
    modifier: Modifier = Modifier,
    accountManager: AccountManager = koinInject(),
) {
    val navController = LocalNavController.current

    ElevatedCard(
        modifier = modifier
            .clip(CardDefaults.elevatedShape)
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    navController.navigate(AppDestination.Channel(channel.id))
                },
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
                enabled = accountManager.loggedIn,
                onClick = onClickSubscribe
            ) {
                Text(stringResource(MR.strings.subscribe))
            }
        }
    }
}