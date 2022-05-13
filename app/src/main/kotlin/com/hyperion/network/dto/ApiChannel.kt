package com.hyperion.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiChannel(
    val contents: Contents,
    val header: Header
) {
    val channelHeaderModal
        get() = header.channelMobileHeaderRenderer.channelHeader.elementRenderer.newElement.type.componentType.model.channelHeaderModel

    @Serializable
    data class Contents(val singleColumnBrowseResultsRenderer: SingleColumnBrowseResultsRenderer)

    @Serializable
    data class SingleColumnBrowseResultsRenderer(val tabs: List<Tab>) {
        @Serializable
        data class Tab(val tabRenderer: TabRenderer) {
            @Serializable
            data class TabRenderer(
                val endpoint: Endpoint,
                val content: Content,
                val selected: Boolean,
                val title: String
            ) {
                @Serializable
                data class Endpoint(val browseEndpoint: BrowseEndpoint) {
                    @Serializable
                    data class BrowseEndpoint(val browseId: String)
                }

                @Serializable
                data class Content(val sectionListRenderer: SectionListRenderer) {
                    @Serializable
                    data class SectionListRenderer(
                        val contents: List<Content>? = null,
                        val continuations: List<Continuation>? = null
                    ) {
                        @Serializable
                        data class Content(val shelfRenderer: ShelfRenderer) {
                            @Serializable
                            data class ShelfRenderer(val content: Content) {
                                @Serializable
                                data class Content(
                                    val horizontalListRenderer: HorizontalListRenderer? = null,
                                    val verticalListRenderer: VerticalListRenderer? = null
                                ) {
                                    @Serializable
                                    data class HorizontalListRenderer(
                                        val items: List<Item>,
                                        val visibleItemCount: Int
                                    ) {
                                        @Serializable
                                        data class Item(
                                            val elementRenderer: ElementRenderer? = null,
                                            val gridChannelRenderer: GridChannelRenderer? = null
                                        ) {
                                            @Serializable
                                            data class ElementRenderer(val newElement: NewElement) {
                                                @Serializable
                                                data class NewElement(val type: Type) {
                                                    @Serializable
                                                    data class Type(val componentType: ComponentType) {
                                                        @Serializable
                                                        data class ComponentType(val model: Model) {
                                                            @Serializable
                                                            data class Model(val gridVideoModel: ApiChannelVideo)
                                                        }
                                                    }
                                                }
                                            }

                                            @Serializable
                                            data class GridChannelRenderer(
                                                val channelId: String,
                                                val navigationEndpoint: NavigationEndpoint,
                                                val shortSubscriberCountText: ShortSubscriberCountText,
                                                val shortVideoCountText: ShortVideoCountText,
                                                val subscriberCountText: SubscriberCountText,
                                                val thumbnail: Thumbnail,
                                                val title: Title,
                                                val videoCountText: VideoCountText
                                            ) {
                                                @Serializable
                                                data class NavigationEndpoint(val browseEndpoint: BrowseEndpoint) {
                                                    @Serializable
                                                    data class BrowseEndpoint(
                                                        val browseId: String,
                                                        val canonicalBaseUrl: String
                                                    )
                                                }

                                                @Serializable
                                                data class ShortSubscriberCountText(val runs: TextRuns)

                                                @Serializable
                                                data class ShortVideoCountText(val runs: TextRuns)

                                                @Serializable
                                                data class SubscriberCountText(val runs: TextRuns)

                                                @Serializable
                                                data class Thumbnail(val thumbnails: List<Thumbnail>) {
                                                    @Serializable
                                                    data class Thumbnail(
                                                        val height: Int,
                                                        val url: String,
                                                        val width: Int
                                                    )
                                                }

                                                @Serializable
                                                data class Title(val runs: TextRuns)

                                                @Serializable
                                                data class VideoCountText(val runs: TextRuns)
                                            }
                                        }
                                    }

                                    @Serializable
                                    data class VerticalListRenderer(val items: List<Item>) {
                                        @Serializable
                                        data class Item(val elementRenderer: ElementRenderer) {
                                            @Serializable
                                            data class ElementRenderer(val newElement: NewElement) {
                                                @Serializable
                                                data class NewElement(val type: Type) {
                                                    @Serializable
                                                    data class Type(val componentType: ComponentType) {
                                                        @Serializable
                                                        data class ComponentType(val model: Model) {
                                                            @Serializable
                                                            data class Model(val videoWithContextModel: ApiVideo)
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
                        data class Continuation(val reloadContinuationData: ReloadContinuationData) {
                            @Serializable
                            data class ReloadContinuationData(val continuation: String)
                        }
                    }
                }
            }
        }
    }

    @Serializable
    data class Header(val channelMobileHeaderRenderer: ChannelMobileHeaderRenderer) {
        @Serializable
        data class ChannelMobileHeaderRenderer(val channelHeader: ChannelHeader) {
            @Serializable
            data class ChannelHeader(val elementRenderer: ElementRenderer) {
                @Serializable
                data class ElementRenderer(val newElement: NewElement) {
                    @Serializable
                    data class NewElement(val type: Type) {
                        @Serializable
                        data class Type(val componentType: ComponentType) {
                            @Serializable
                            data class ComponentType(val model: Model) {
                                @Serializable
                                data class Model(val channelHeaderModel: ChannelHeaderModel) {
                                    @Serializable
                                    data class ChannelHeaderModel(
                                        val channelBanner: ChannelBanner,
                                        val channelProfile: ChannelProfile
                                    ) {
                                        @Serializable
                                        data class ChannelBanner(val image: ApiImage)

                                        @Serializable
                                        data class ChannelProfile(
                                            val avatarData: AvatarData,
                                            val descriptionPreview: DescriptionPreview,
                                            val metadata: Metadata,
                                            val title: String
                                        ) {
                                            @Serializable
                                            data class AvatarData(val avatar: Avatar) {
                                                @Serializable
                                                data class Avatar(val image: ApiImage)
                                            }

                                            @Serializable
                                            data class DescriptionPreview(val description: String = "")

                                            @Serializable
                                            data class Metadata(
                                                val joinDateText: String,
                                                val subscriberCountText: String,
                                                val videosCountText: String
                                            )
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
}

@Serializable
data class ApiChannelVideo(
    val videoData: VideoData,
    val onTap: OnTap
) {
    @Serializable
    data class VideoData(
        val metadata: Metadata,
        val thumbnail: Thumbnail
    ) {
        @Serializable
        data class Metadata(
            val metadataDetails: String,
            val title: String
        )

        @Serializable
        data class Thumbnail(
            val image: ApiImage,
            val timestampText: String? = null
        )
    }

    @Serializable
    data class OnTap(val innertubeCommand: InnerTubeCommand) {
        @Serializable
        data class InnerTubeCommand(val watchEndpoint: WatchEndpoint? = null) {
            @Serializable
            data class WatchEndpoint(val videoId: String)
        }
    }
}