package dev.zt64.hyperion.domain.manager

import org.koin.core.component.KoinComponent

internal expect class DownloadManager(preferencesManager: PreferencesManager) : KoinComponent {
    fun download()
}