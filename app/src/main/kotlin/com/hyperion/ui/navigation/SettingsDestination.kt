package com.hyperion.ui.navigation

import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.hyperion.R
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
enum class SettingsDestination(
    val icon: @RawValue ImageVector,
    @StringRes val label: Int
) : Parcelable {
    MAIN(Icons.Default.Settings, R.string.settings),
    GENERAL(Icons.Default.Settings, R.string.general),
    APPEARANCE(Icons.Default.Palette, R.string.appearance),
    ACCOUNTS(Icons.Default.ManageAccounts, R.string.accounts),
    SPONSOR_BLOCK(Icons.Default.MoneyOff, R.string.sponsor_block),
    BACKUP_RESTORE(Icons.Default.SettingsBackupRestore, R.string.backup_restore),
    ABOUT(Icons.Default.Info, R.string.about)
}