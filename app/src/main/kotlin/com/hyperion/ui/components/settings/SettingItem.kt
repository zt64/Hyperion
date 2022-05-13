package com.hyperion.ui.components.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingItem(
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit) = { },
    text: @Composable () -> Unit,
    secondaryText: @Composable (() -> Unit) = { },
    trailing: @Composable (() -> Unit) = { },
) {
    Row(
        modifier = modifier
            .heightIn(min = 64.dp)
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()

        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            ProvideTextStyle(MaterialTheme.typography.titleMedium) {
                text()
            }
            ProvideTextStyle(MaterialTheme.typography.bodyMedium) {
                secondaryText()
            }
        }

        Spacer(Modifier.weight(1f, true))

        trailing()
    }
}