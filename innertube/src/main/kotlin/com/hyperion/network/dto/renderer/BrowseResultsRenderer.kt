package com.hyperion.network.dto.renderer

import com.hyperion.network.dto.ApiNavigationEndpoint
import kotlinx.serialization.Serializable

@Serializable
internal data class BrowseResultsRenderer<T>(private val tabs: List<Tab<T>>) {
    val content = tabs[0].tabRenderer.content.sectionListRenderer

    @Serializable
    data class Tab<T>(val tabRenderer: TabRenderer<T>)

    @Serializable
    data class TabRenderer<T>(
        val content: TabContent<T>,
        val endpoint: ApiNavigationEndpoint? = null
    )

    @Serializable
    data class TabContent<T>(val sectionListRenderer: T)
}