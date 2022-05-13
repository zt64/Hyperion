package com.hyperion.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiNext(val contents: Contents) {
    @Serializable
    data class Contents(val singleColumnWatchNextResults: SingleColumnWatchNextResults) {
        @Serializable
        data class SingleColumnWatchNextResults(val results: Results) {
            @Serializable
            data class Results(val results: Results) {
                @Serializable
                data class Results(val contents: List<Content>) {
                    @Serializable
                    data class Content(val slimVideoMetadataSectionRenderer: SlimVideoMetadataSectionRenderer? = null) {
                        @Serializable
                        data class SlimVideoMetadataSectionRenderer(val contents: List<Content>) {
                            @Serializable
                            data class Content(val elementRenderer: ElementRenderer) {
                                @Serializable
                                data class ElementRenderer(val newElement: NewElement) {
                                    @Serializable
                                    data class NewElement(val type: Type) {
                                        @Serializable
                                        data class Type(val componentType: ComponentType) {
                                            @Serializable
                                            data class ComponentType(val model: Model)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Serializable
    data class Model(
        val videoMetadataModel: VideoMetadataModel? = null,
        val channelBarModel: ChannelBarModel? = null,
        val videoActionBarModel: VideoActionBarModel? = null
    ) {
        @Serializable
        data class VideoMetadataModel(val videoMetadata: VideoMetadata) {
            @Serializable
            data class VideoMetadata(
                val subtitleData: SubtitleData,
                val title: Title
            ) {
                @Serializable
                data class SubtitleData(
                    val viewCount: ViewCount,
                    val viewCountEntity: ViewCountEntity,
                    val viewCountLength: Int
                ) {
                    @Serializable
                    data class ViewCount(val content: String)

                    @Serializable
                    data class ViewCountEntity(val key: String)
                }

                @Serializable
                data class Title(val content: String)
            }
        }

        @Serializable
        data class ChannelBarModel(val videoChannelBarData: VideoChannelBarData) {
            @Serializable
            data class VideoChannelBarData(
                val avatar: Avatar,
                val subtitle: String? = null
            ) {
                @Serializable
                data class Avatar(val image: Image) {
                    @Serializable
                    data class Image(val sources: List<Source>) {
                        @Serializable
                        data class Source(val url: String)
                    }
                }
            }
        }

        @Serializable
        data class VideoActionBarModel(val buttons: List<Button>) {
            @Serializable
            data class Button(val likeButton: LikeButton? = null) {
                @Serializable
                data class LikeButton(val buttonData: ButtonData) {
                    @Serializable
                    data class ButtonData(val defaultButton: DefaultButton) {
                        @Serializable
                        data class DefaultButton(val title: String)
                    }
                }
            }
        }
    }
}