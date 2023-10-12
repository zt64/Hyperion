package dev.zt64.hyperion.ui.screen.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.MR
import dev.zt64.hyperion.domain.manager.PreferencesManager

context(ColumnScope)
@Composable
fun BackupRestoreScreen(preferences: PreferencesManager) {
    BackupRestoreScreen(
        preferences = preferences,
        onClickRestore = { /*TODO*/ },
        onClickBackup = { /*TODO*/ }
    )
}

@Composable
fun BackupRestoreScreen(
    preferences: PreferencesManager,
    onClickRestore: () -> Unit,
    onClickBackup: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
    ) {
        OutlinedButton(
            modifier = Modifier
                .weight(1f, true)
                .widthIn(max = 120.dp),
            onClick = onClickRestore
        ) {
            Text(stringResource(MR.strings.restore))
        }

        OutlinedButton(
            modifier = Modifier
                .weight(1f, true)
                .widthIn(max = 120.dp),
            onClick = onClickBackup
        ) {
            Text(stringResource(MR.strings.backup))
        }
    }
}