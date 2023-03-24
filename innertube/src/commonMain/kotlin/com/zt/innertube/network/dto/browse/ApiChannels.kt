package com.zt.innertube.network.dto.browse

import com.zt.innertube.network.dto.ApiImage
import com.zt.innertube.network.dto.ApiText
import com.zt.innertube.network.dto.renderer.ItemSectionRenderer
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

@Serializable
internal data class ApiChannelsContinuation(
    override val onResponseReceivedActions: List<ContinuationContents<ApiChannels.SectionContent>>
) : ApiBrowseContinuation()