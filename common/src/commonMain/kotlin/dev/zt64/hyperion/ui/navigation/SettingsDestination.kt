package dev.zt64.hyperion.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.ui.graphics.vector.ImageVector
import cafe.adriel.voyager.core.screen.Screen
import dev.icerock.moko.resources.StringResource
import dev.zt64.hyperion.resources.MR
import dev.zt64.hyperion.ui.screen.settings.AboutScreen
import dev.zt64.hyperion.ui.screen.settings.AccountsScreen
import dev.zt64.hyperion.ui.screen.settings.AppearanceScreen
import dev.zt64.hyperion.ui.screen.settings.BackupRestoreScreen
import dev.zt64.hyperion.ui.screen.settings.DearrowScreen
import dev.zt64.hyperion.ui.screen.settings.DevScreen
import dev.zt64.hyperion.ui.screen.settings.GeneralScreen
import dev.zt64.hyperion.ui.screen.settings.GesturesScreen
import dev.zt64.hyperion.ui.screen.settings.NotificationsScreen
import dev.zt64.hyperion.ui.screen.settings.SponsorBlockScreen

internal sealed interface SettingsDestination {
    val label: StringResource
}

enum class SettingsSection(
    val icon: ImageVector,
    override val label: StringResource,
    val screen: () -> Screen
) : SettingsDestination {
    GENERAL(Icons.Default.Settings, MR.strings.general, ::GeneralScreen),
    APPEARANCE(Icons.Default.Palette, MR.strings.appearance, ::AppearanceScreen),
    GESTURES(Icons.Default.Gesture, MR.strings.gestures, ::GesturesScreen),
    NOTIFICATIONS(Icons.Default.Notifications, MR.strings.notifications, ::NotificationsScreen),
    ACCOUNTS(Icons.Default.ManageAccounts, MR.strings.accounts, ::AccountsScreen),
    SPONSOR_BLOCK(Icons.Default.MoneyOff, MR.strings.sponsor_block, ::SponsorBlockScreen),
    DEARROW(Icons.Default.Settings, MR.strings.dearrow, ::DearrowScreen),
    BACKUP_RESTORE(
        Icons.Default.SettingsBackupRestore,
        MR.strings.backup_restore,
        ::BackupRestoreScreen
    ),
    ABOUT(Icons.Default.Info, MR.strings.about, ::AboutScreen),
    DEVELOPER(Icons.Default.DeveloperMode, MR.strings.developer, ::DevScreen)
}