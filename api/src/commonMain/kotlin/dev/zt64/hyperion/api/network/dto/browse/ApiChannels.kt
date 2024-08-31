package dev.zt64.hyperion.api.network.dto.browse

import dev.zt64.hyperion.api.network.dto.ApiText
import dev.zt64.hyperion.api.network.dto.Continuation
import dev.zt64.hyperion.api.network.dto.renderer.ItemSectionRenderer
import kotlinx.serialization.Serializable

@Serializable
internal data class ApiChannels(override val contents: Contents<SectionContent>) : ApiBrowse() {
    @Serializable
    data class SectionContent(val itemSectionRenderer: ItemSectionRenderer<Content>) {
        @Serializable
        data class Content(val channelListItemRenderer: ChannelListItemRenderer)
    }

    @Serializable
    data class ChannelListItemRenderer(
        val channelId: String,
        val thumbnail: dev.zt64.hyperion.api.network.dto.ApiImage,
        val title: ApiText
    )
}

internal typealias ApiChannelsContinuation = Continuation<ApiChannels.SectionContent>