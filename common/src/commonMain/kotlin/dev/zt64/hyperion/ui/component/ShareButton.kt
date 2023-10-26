package dev.zt64.hyperion.ui.component

import androidx.compose.runtime.Composable

/**
 * A button that shares the given content.
 * On Android, this will open the system share sheet.
 * On Desktop, this will copy the content to the clipboard.
 *
 * @param content The content to share.
 * @param label Optional label to show in the share sheet.
 */
@Composable
expect fun ShareButton(content: String, label: String? = null)

@Composable
expect fun FilledTonalShareButton(content: String, label: String? = null)

@Composable
internal expect fun ShareButtonIcon()