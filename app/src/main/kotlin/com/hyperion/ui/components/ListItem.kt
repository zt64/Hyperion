package com.hyperion.ui.components

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier

// TODO: Switch to custom implementation and remove dependency on Material 2
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ListItem(
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    secondaryText: @Composable (() -> Unit)? = null,
    singleLineSecondaryText: Boolean = true,
    overlineText: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    text: @Composable () -> Unit
) = androidx.compose.material.ListItem(
    modifier = modifier,
    icon = icon,
    overlineText = if (overlineText != null) {
        {
            CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
                LocalTextStyle provides MaterialTheme.typography.labelMedium
            ) {
                overlineText()
            }
        }
    } else null,
    text = {
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.onSurface,
            LocalTextStyle provides MaterialTheme.typography.bodyLarge
        ) {
            text()
        }
    },
    secondaryText = if (secondaryText != null) {
        {
            CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
                LocalTextStyle provides MaterialTheme.typography.bodyMedium
            ) {
                secondaryText()
            }
        }
    } else null,
    trailing = trailing,
    singleLineSecondaryText = singleLineSecondaryText,
)