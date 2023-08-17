package dev.zt64.hyperion.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import dev.icerock.moko.parcelize.IgnoredOnParcel
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import dev.icerock.moko.resources.StringResource
import dev.zt64.hyperion.MR

sealed interface SettingsDestination {
    val label: StringResource
}

enum class SettingsSection(
    val icon: ImageVector,
    override val label: StringResource,
) : SettingsDestination {
    GENERAL(Icons.Default.Settings, MR.strings.general),
    APPEARANCE(Icons.Default.Palette, MR.strings.appearance),
    GESTURES(Icons.Default.Gesture, MR.strings.gestures),
    NOTIFICATIONS(Icons.Default.Notifications, MR.strings.notifications),
    ACCOUNTS(Icons.Default.ManageAccounts, MR.strings.accounts),
    SPONSOR_BLOCK(Icons.Default.MoneyOff, MR.strings.sponsor_block),
    BACKUP_RESTORE(Icons.Default.SettingsBackupRestore, MR.strings.backup_restore),
    ABOUT(Icons.Default.Info, MR.strings.about);

    @Parcelize
    companion object : SettingsDestination, Parcelable {
        @IgnoredOnParcel
        override val label = MR.strings.settings
    }
}