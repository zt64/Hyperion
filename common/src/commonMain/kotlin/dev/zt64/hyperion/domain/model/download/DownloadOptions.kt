package dev.zt64.hyperion.domain.model.download

import androidx.compose.runtime.Stable

/**
 * Download options used for passing options selected by the user to the download manager.
 *
 * @property id
 */
@Stable
class DownloadOptions(val id: String)