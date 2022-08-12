package com.hyperion.network.dto

import com.hyperion.network.dto.renderer.ElementRenderer
import com.hyperion.network.dto.renderer.ItemSectionRenderer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

@Serializable
data class ApiSearch(
    val contents: Contents? = null,
    val continuationContents: ContinuationContents? = null,
    val estimatedResults: String
) {
    @Serializable(with = Model.Serializer::class)
    sealed interface Model {
        companion object Serializer : JsonContentPolymorphicSerializer<Model>(Model::class) {
            override fun selectDeserializer(element: JsonElement) = when {
                "videoWithContextSlotsModel" in element.jsonObject -> VideoWithContextSlots.serializer()
                "videoWithContextModel" in element.jsonObject -> VideoWithContext.serializer()
                "cellDividerModel" in element.jsonObject -> CellDivider.serializer()
                "inlineShortsModel" in element.jsonObject -> InlineShorts.serializer()
                "shelfHeaderModel" in element.jsonObject -> ShelfHeader.serializer()
                "compactTvfilmItemModel" in element.jsonObject -> Unknown.serializer()
                "textSearchAdWithDescriptionFirstModel" in element.jsonObject -> Unknown.serializer()
                "shortsShelfModel" in element.jsonObject -> Unknown.serializer()
                "horizontalVideoShelfModel" in element.jsonObject -> Unknown.serializer()
                "postShelfModel" in element.jsonObject -> Unknown.serializer()
                else -> throw NoWhenBranchMatchedException(element.jsonObject.toString())
            }
        }
    }

    @Serializable
    data class VideoWithContextSlots(val videoWithContextSlotsModel: Model) : Model {
        @Serializable
        data class Model(val videoWithContextData: VideoWithContextData)

        @Serializable
        data class VideoWithContextData(
            val onTap: OnTap,
            val videoData: VideoData
        )

        @Serializable
        data class OnTap(val innertubeCommand: InnertubeCommand) {
            @Serializable
            data class InnertubeCommand(
                val browseEndpoint: ApiBrowseEndpoint? = null,
                val watchEndpoint: WatchEndpoint? = null
            ) {
                @Serializable
                data class WatchEndpoint(
                    val continuePlayback: Boolean = false,
                    val params: String,
                    val playlistId: String? = null,
                    val videoId: String? = null
                )
            }
        }

        @Serializable
        data class VideoData(
            val avatar: ApiAvatar? = null,
            val metadata: Metadata,
            val thumbnail: Thumbnail
        ) {
            @Serializable
            data class Metadata(
                val title: String,
                val byline: String? = null,
                val metadataDetails: String? = null,
                val isPlaylistMix: Boolean = false,
                val isVideoWithContext: Boolean? = null,
            )

            @Serializable
            data class Thumbnail(
                val isMix: Boolean? = null,
                val isPlaylist: Boolean? = null,
                val isVideoWithContext: Boolean,
                val timestampText: String? = null,
                val videoCount: String? = null
            )
        }
    }

    @Serializable
    object VideoWithContext : Model

    @Serializable
    object CellDivider : Model

    @Serializable
    object InlineShorts : Model

    @Serializable
    object ShelfHeader : Model

    @Serializable
    object Unknown : Model

    @Serializable
    data class Contents(val sectionListRenderer: SectionListRenderer)

    @Serializable
    data class ContinuationContents(val sectionListContinuation: SectionListRenderer)

    @Serializable
    data class SectionListRenderer(
        val contents: List<Content>,
        val continuations: List<ApiContinuation> = emptyList()
    ) {
        @Serializable
        data class Content(
            val itemSectionRenderer: ItemSectionRenderer<Renderer>? = null
        ) {
            @Serializable(with = Renderer.Serializer::class)
            sealed interface Renderer {
                companion object Serializer : JsonContentPolymorphicSerializer<Renderer>(Renderer::class) {
                    override fun selectDeserializer(element: JsonElement) = when {
                        "chipCloudRenderer" in element.jsonObject -> ChipCloud.serializer()
                        "compactChannelRenderer" in element.jsonObject -> CompactChannel.serializer()
                        "elementRenderer" in element.jsonObject -> Element.serializer()
                        "messageRenderer" in element.jsonObject -> Message.serializer()
                        else -> throw NoWhenBranchMatchedException(element.jsonObject.toString())
                    }
                }

                @Serializable
                data class ChipCloud(val chipCloudRenderer: ChipCloudRenderer) : Renderer

                @Serializable
                data class CompactChannel(val compactChannelRenderer: CompactChannelRenderer) : Renderer

                @Serializable
                data class Element(val elementRenderer: ElementRenderer<Model>) : Renderer

                @Serializable
                data class Message(val messageRenderer: MessageRenderer) : Renderer
            }

            @Serializable
            data class ChipCloudRenderer(val chips: List<Chip> = emptyList()) {
                @Serializable
                data class Chip(val chipCloudChipRenderer: ChipRenderer) {
                    @Serializable
                    data class ChipRenderer(
                        val isSelected: Boolean,
                        val location: String,
                        val navigationEndpoint: NavigationEndpoint? = null,
                        val text: ApiText
                    ) {
                        @Serializable
                        data class NavigationEndpoint(val searchEndpoint: SearchEndpoint) {
                            @Serializable
                            data class SearchEndpoint(
                                val originalChipQuery: String,
                                val params: String,
                                val query: String
                            )
                        }
                    }
                }
            }

            @Serializable
            data class CompactChannelRenderer(
                val channelId: String,
                val displayName: ApiText,
                val navigationEndpoint: ApiNavigationEndpoint,
                val subscriberCountText: ApiText? = null,
                val thumbnail: ApiThumbnail,
                val title: ApiText,
                val videoCountText: ApiText? = null
            )

            @Serializable
            data class MessageRenderer(val text: ApiText)
        }
    }
}