package dev.zt64.innertube.network.dto.browse

import dev.zt64.innertube.network.dto.ApiImage
import dev.zt64.innertube.network.dto.ApiText
import dev.zt64.innertube.network.dto.Continuation
import dev.zt64.innertube.network.dto.renderer.ItemSectionRenderer
import kotlinx.serialization.Serializable

@Serializable
internal data class ApiChannels(
    override val contents: Contents<SectionContent>
) : ApiBrowse() {
    @Serializable
    data class SectionContent(val itemSectionRenderer: ItemSectionRenderer<Content>) {
        @Serializable
        data class Content(val channelListItemRenderer: ChannelListItemRenderer)
    }

    @Serializable
    data class ChannelListItemRenderer(
        val channelId: String,
        val thumbnail: ApiImage,
        val title: ApiText
    )
}

internal typealias ApiChannelsContinuation = Continuation<ApiChannels.SectionContent>