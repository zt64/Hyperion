package dev.zt64.hyperion.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.ui.graphics.vector.ImageVector
import dev.icerock.moko.resources.StringResource
import dev.zt64.hyperion.resources.MR

internal sealed interface SettingsDestination {
    val label: StringResource
}

enum class SettingsSection(val icon: ImageVector, override val label: StringResource) :
    SettingsDestination {
    GENERAL(Icons.Default.Settings, MR.strings.general),
    APPEARANCE(Icons.Default.Palette, MR.strings.appearance),
    GESTURES(Icons.Default.Gesture, MR.strings.gestures),
    NOTIFICATIONS(Icons.Default.Notifications, MR.strings.notifications),
    ACCOUNTS(Icons.Default.ManageAccounts, MR.strings.accounts),
    SPONSOR_BLOCK(Icons.Default.MoneyOff, MR.strings.sponsor_block),
    DEARROW(Icons.Default.Settings, MR.strings.dearrow),
    BACKUP_RESTORE(Icons.Default.SettingsBackupRestore, MR.strings.backup_restore),
    ABOUT(Icons.Default.Info, MR.strings.about);

    companion object : SettingsDestination {
        override val label = MR.strings.settings
    }
}