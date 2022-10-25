package com.hyperion.network.dto

import com.hyperion.network.dto.renderer.ElementRenderer
import com.hyperion.network.dto.renderer.ItemSectionRenderer
import com.hyperion.network.dto.renderer.ListRenderer
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
class ApiTagParams(
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
class ApiTag(
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
        val shelfRenderer: ShelfRenderer? = null
    ) {
        @Serializable
        data class Content(val elementRenderer: ElementRenderer<Model>) {
            @Serializable
            data class Model(val hashtagHeaderModel: HashtagHeaderModel) {
                @Serializable
                data class HashtagHeaderModel(val renderer: Renderer) {
                    @Serializable
                    data class Renderer(
                        val avatarFacepile: List<ElementsImageContainer> = emptyList(),
                        val backgroundColor: Long,
                        val backgroundImage: ElementsImageContainer? = null,
                        val hashtag: ElementsAttributedContainer,
                        val hashtagInfoText: ElementsAttributedContainer? = null
                    ) {
                        @Serializable
                        data class ElementsImageContainer(val elementsImage: ApiImage)

                        @Serializable
                        data class ElementsAttributedContainer(val elementsAttributedString: ElementsAttributedString) {
                            @Serializable
                            data class ElementsAttributedString(val content: String)
                        }
                    }
                }
            }
        }

        @Serializable
        data class ShelfRenderer(val content: ShelfContent)

        @Serializable
        data class ShelfContent(val horizontalListRenderer: ListRenderer<Item>) {
            @Serializable
            data class Item(val elementRenderer: ElementRenderer<Model>)
        }
    }

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
                val thumbnail: Thumbnail
            ) {
                @Serializable
                data class Metadata(
                    val byline: String,
                    val isVideoWithContext: Boolean,
                    val metadataDetails: String,
                    val title: String
                )

                @Serializable
                data class Thumbnail(
                    val image: ApiImage,
                    val isVideoWithContext: Boolean,
                    val timestampText: String? = null
                )
            }
        }
    }
}

@Serializable
data class ApiTagContinuation(
    override val continuationContents: ContinuationContents<ApiTag.ContentItem>
) : ApiBrowseContinuation()