package dev.zt64.hyperion.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.olshevski.navigation.reimagined.navigate
import dev.zt64.hyperion.domain.manager.AccountManager
import dev.zt64.hyperion.ui.navigation.AppDestination
import dev.zt64.hyperion.ui.navigation.LocalNavController
import dev.zt64.innertube.domain.model.DomainChannelPartial
import org.koin.compose.koinInject

@Composable
fun ChannelCard(
    modifier: Modifier = Modifier,
    channel: DomainChannelPartial,
    onLongClick: () -> Unit,
    onClickSubscribe: () -> Unit,
) {
    val navController = LocalNavController.current
    val accountManager: AccountManager = koinInject()

    ChannelCard(
        channel = channel,
        isLoggedIn = accountManager.loggedIn,
        onClick = {
            navController.navigate(AppDestination.Channel(channel.id))
        },
        onClickSubscribe = onClickSubscribe,
        onLongClick = onLongClick,
        modifier = modifier,
    )
}