package dev.zt64.hyperion.domain.manager

import org.koin.core.component.KoinComponent

internal expect class UrlManager() : KoinComponent {
    fun openUrl(url: String)
}