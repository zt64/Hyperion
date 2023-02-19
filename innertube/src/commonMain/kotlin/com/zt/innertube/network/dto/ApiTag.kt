package com.zt.innertube.network.dto

import com.zt.innertube.network.dto.browse.ApiBrowse
import com.zt.innertube.network.dto.browse.ApiBrowseContinuation
import com.zt.innertube.network.dto.renderer.ElementRenderer
import com.zt.innertube.network.dto.renderer.ItemSectionRenderer
import com.zt.innertube.network.dto.renderer.ListRenderer
import com.zt.innertube.network.dto.renderer.ShelfRenderer
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
internal data class ApiTagParams(
    @ProtoNumber(93)
    val context: Context
) {
    @Serializable
    data class Context(
        @ProtoNumber(1)
        val tag: String
    )
}

@Serializable
internal data class ApiTag(
    val header: Header,
    override val contents: Contents<ContentItem>
) : ApiBrowse() {
    @Serializable
    data class Header(val translucentHeaderRenderer: TranslucentHeaderRenderer) {
        @Serializable
        data class TranslucentHeaderRenderer(
            val actionBarColor: Int,
            val primaryTitleColor: Long,
            val shouldHideTitleOnTranslucentHeader: Boolean,
            val statusBarColor: Long,
            val title: ApiText
        )
    }

    @Serializable
    data class ContentItem(
        val itemSectionRenderer: ItemSectionRenderer<Content>? = null,
        val shelfRenderer: ShelfRenderer<ShelfContent>? = null
    ) {
        @Serializable
        data class Content(val elementRenderer: ElementRenderer<Model>) {
            @Serializable
            data class Model(val hashtagHeaderModel: HashtagHeaderModel) {
                @Serializable
                data class HashtagHeaderModel(val renderer: HashtagRenderer)
            }
        }

        @Serializable
        data class ShelfContent(val horizontalListRenderer: ListRenderer<Item>) {
            @Serializable
            data class Item(val elementRenderer: ElementRenderer<Model>)
        }
    }

    @Serializable
    data class HashtagRenderer(
        val avatarFacepile: List<ImageContainer> = emptyList(),
        val backgroundColor: Long,
        val backgroundImage: ImageContainer? = null,
        val hashtag: ElementsAttributedText,
        val hashtagInfoText: ElementsAttributedText? = null
    )

    @Serializable
    data class Model(val videoWithContextModel: VideoWithContextModel) {
        @Serializable
        data class VideoWithContextModel(val videoWithContextData: VideoWithContextData) {
            @Serializable
            data class VideoWithContextData(
                val onTap: OnTap<InnertubeCommand>,
                val videoData: VideoData
            )

            @Serializable
            data class InnertubeCommand(
                val reelWatchEndpoint: ApiWatchEndpoint? = null,
                val watchEndpoint: ApiWatchEndpoint? = null
            )

            @Serializable
            data class VideoData(
                val metadata: Metadata,
                val thumbnail: ApiThumbnailTimestamp
            ) {
                @Serializable
                data class Metadata(
                    val byline: String,
                    val isVideoWithContext: Boolean,
                    val metadataDetails: String,
                    val title: String
                )
            }
        }
    }
}

@Serializable
internal data class ApiTagContinuation(
    override val continuationContents: ContinuationContents<ApiTag.ContentItem>
) : ApiBrowseContinuation()