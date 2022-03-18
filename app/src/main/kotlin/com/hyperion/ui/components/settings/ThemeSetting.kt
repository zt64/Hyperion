/*
 * Copyright (c) 2022 Juby210 & zt
 * Licensed under the Open Software License version 3.0
 */

package com.hyperion.ui.components.settings

import androidx.compose.foundation.clickable
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hyperion.R
import com.hyperion.preferences.Prefs
import com.hyperion.ui.components.ListItem

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ThemeSetting(
    onClick: () -> Unit,
) = ListItem(
    modifier = Modifier.clickable { onClick() },
    text = { Text(stringResource(R.string.theme)) },
    secondaryText = { Text(stringResource(R.string.theme_setting_description)) },
    trailing = {
        FilledTonalButton(
            onClick = onClick
        ) {
            Text(Prefs.theme.displayName)
        }
    }
)