package com.zt.innertube.network.dto.browse

import com.zt.innertube.network.dto.*
import com.zt.innertube.network.dto.renderer.ElementRenderer
import com.zt.innertube.network.dto.renderer.ItemSectionRenderer
import kotlinx.serialization.Serializable

@Serializable
internal data class ApiSubscriptions(
    override val contents: Contents<SectionContent>
) : ApiBrowse() {
    @Serializable
    data class SectionContent(val itemSectionRenderer: ItemSectionRenderer<Content>? = null) {
        @Serializable
        data class Content(val elementRenderer: ElementRenderer<Model>)
    }

    @Serializable
    data class Model(val videoWithContextModel: VideoWithContextModel) {
        @Serializable
        data class VideoWithContextModel(val videoWithContextData: VideoWithContextData) {
            @Serializable
            data class VideoWithContextData(
                val onTap: OnTap<ApiWatchCommand>,
                val videoData: VideoData
            ) {
                @Serializable
                data class VideoData(
                    val avatar: ApiAvatar,
                    val metadata: Metadata,
                    val thumbnail: Thumbnail
                ) {
                    @Serializable
                    data class Metadata(
                        val title: String,
                        val metadataDetails: String,
                        val isVideoWithContext: Boolean
                    )

                    @Serializable
                    data class Thumbnail(
                        val image: ApiImage,
                        val isVideoWithContext: Boolean,
                        val timestampText: String
                    )
                }
            }
        }
    }
}

@Serializable
internal class ApiSubscriptionsContinuation(
    override val contents: ContinuationContents<ApiSubscriptions.SectionContent>
) : ApiBrowseContinuation()