package com.zt.innertube.network.dto.browse

import com.zt.innertube.network.dto.*
import com.zt.innertube.network.dto.renderer.ElementRenderer
import com.zt.innertube.network.dto.renderer.ItemSectionRenderer
import kotlinx.serialization.Serializable

@Serializable
internal data class ApiRecommended(
    override val contents: Contents<SectionContent>
) : ApiBrowse() {
    @Serializable
    data class SectionContent(val itemSectionRenderer: ItemSectionRenderer<Content>) {
        @Serializable
        data class Content(val elementRenderer: ElementRenderer<Model>? = null) {
            @Serializable
            data class Model(
                val shortsShelfModel: ShortsShelfModel? = null,
                val videoWithContextModel: ApiVideo? = null
            )
        }
    }
}

@Serializable
internal data class ShortsShelfModel(val items: List<Item>) {
    @Serializable
    data class Item(
        val bottomText: String,
        val id: String,
        val onTap: OnTap,
        val thumbnail: ApiImage,
        val videoTitle: String
    ) {
        @Serializable
        data class OnTap(val innertubeCommand: InnertubeCommand) {
            @Serializable
            data class InnertubeCommand(val reelWatchEndpoint: ReelWatchEndpoint) {
                @Serializable
                data class ReelWatchEndpoint(
                    val overlay: Overlay,
                    val thumbnail: ApiImage,
                    val videoId: String
                ) {
                    @Serializable
                    data class Overlay(val reelPlayerOverlayRenderer: ReelPlayerOverlayRenderer? = null) {
                        @Serializable
                        data class ReelPlayerOverlayRenderer(val reelPlayerHeaderSupportedRenderers: ReelPlayerHeaderSupportedRenderers? = null) {
                            @Serializable
                            data class ReelPlayerHeaderSupportedRenderers(val reelPlayerHeaderRenderer: ReelPlayerHeaderRenderer) {
                                @Serializable
                                data class ReelPlayerHeaderRenderer(
                                    val channelNavigationEndpoint: ApiNavigationEndpoint,
                                    val channelThumbnail: ApiImage,
                                    val channelTitleText: ApiText,
                                    val reelTitleText: ApiText,
                                    val timestampText: ApiText
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Serializable
internal data class ApiRecommendedContinuation(
    override val continuationContents: ContinuationContents<ApiRecommended.SectionContent>
) : ApiBrowseContinuation()