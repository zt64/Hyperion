package dev.zt64.hyperion.ui.screen.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import dev.zt64.hyperion.api.network.service.InnerTubeService
import dev.zt64.hyperion.domain.manager.PreferencesManager
import org.koin.compose.koinInject

class DevScreen : Screen {
    @Composable
    override fun Content() {
        val innerTubeService = koinInject<InnerTubeService>()
        val preferences = koinInject<PreferencesManager>()

        TextField(
            value = preferences.visitorData.orEmpty(),
            onValueChange = { preferences.visitorData = it },
            trailingIcon = {
                IconButton(
                    onClick = {
                        preferences.visitorData = InnerTubeService.generateVisitorData()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null
                    )
                }
            }
        )
    }
}