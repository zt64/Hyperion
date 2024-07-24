package dev.zt64.innertube.network.dto.browse

import dev.zt64.innertube.network.dto.ApiNext
import dev.zt64.innertube.network.dto.Continuation
import dev.zt64.innertube.network.dto.renderer.ItemSectionRenderer
import kotlinx.serialization.Serializable

@Serializable
internal data class ApiHistory(override val contents: Contents<SectionContent>) : ApiBrowse() {
    @Serializable
    data class SectionContent(val itemSectionRenderer: ItemSectionRenderer<Content>? = null) {
        @Serializable
        data class Content(val compactVideoRenderer: ApiNext.CompactVideoRenderer)
    }
}

internal typealias ApiHistoryContinuation = Continuation<ApiHistory.SectionContent>