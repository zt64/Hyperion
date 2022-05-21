package com.hyperion.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiSearch(
    val contents: Contents? = null,
    val continuationContents: ContinuationContents? = null,
    val estimatedResults: String
) {
    @Serializable
    data class Contents(val sectionListRenderer: SectionListRenderer)

    @Serializable
    data class ContinuationContents(val sectionListContinuation: SectionListRenderer)

    @Serializable
    data class SectionListRenderer(
        val contents: List<Content>,
        val continuations: List<ApiContinuation>
    ) {
        @Serializable
        data class Content(val itemSectionRenderer: ItemSectionRenderer? = null) {
            @Serializable
            data class ItemSectionRenderer(val contents: List<Content>) {
                @Serializable
                data class Content(
                    val compactVideoRenderer: CompactVideoRenderer? = null,
                    val compactChannelRenderer: CompactChannelRenderer? = null,
                    val compactPlaylistRenderer: CompactPlaylistRenderer? = null
                ) {
                    @Serializable
                    data class CompactVideoRenderer(
                        val badges: List<Badge> = emptyList(),
                        val channelThumbnail: ApiThumbnail,
                        val lengthText: ApiText? = null,
                        val longBylineText: BylineText,
                        val navigationEndpoint: NavigationEndpoint,
                        val publishedTimeText: ApiText? = null,
                        val shortBylineText: BylineText,
                        val shortViewCountText: ApiText? = null,
                        val thumbnail: ApiThumbnail,
                        val thumbnailOverlays: List<ThumbnailOverlay>,
                        val title: ApiText,
                        val videoId: String,
                        val viewCountText: ApiText? = null
                    ) {
                        @Serializable
                        data class Badge(val textBadge: TextBadge) {
                            @Serializable
                            data class TextBadge(val label: ApiText)
                        }

                        @Serializable
                        data class BylineText(val runs: List<Run>) {
                            @Serializable
                            data class Run(
                                val navigationEndpoint: NavigationEndpoint,
                                val text: String
                            ) {
                                @Serializable
                                data class NavigationEndpoint(val browseEndpoint: BrowseEndpoint) {
                                    @Serializable
                                    data class BrowseEndpoint(
                                        val browseId: String,
                                        val canonicalBaseUrl: String
                                    )
                                }
                            }
                        }
                    }

                    @Serializable
                    data class CompactChannelRenderer(
                        val channelId: String,
                        val displayName: ApiText,
                        val navigationEndpoint: NavigationEndpoint,
                        val subscribeButton: SubscribeButton,
                        val subscriberCountText: ApiText,
                        val thumbnail: ApiThumbnail,
                        val title: ApiText,
                        val videoCountText: ApiText
                    ) {
                        @Serializable
                        data class SubscribeButton(val subscribeButtonRenderer: SubscribeButtonRenderer) {
                            @Serializable
                            data class SubscribeButtonRenderer(
                                val buttonText: ApiText,
                                val channelId: String,
                                val enabled: Boolean,
                                val onSubscribeEndpoints: List<OnSubscribeEndpoint>,
                                val onUnsubscribeEndpoints: List<OnUnsubscribeEndpoint>,
                                val subscribed: Boolean,
                                val subscribedButtonText: ApiText,
                                val unsubscribeButtonText: ApiText,
                                val unsubscribeMessage: UnsubscribeMessage,
                                val unsubscribedButtonText: ApiText
                            ) {
                                @Serializable
                                data class OnSubscribeEndpoint(val subscribeEndpoint: SubscriptionEndpoint)

                                @Serializable
                                data class OnUnsubscribeEndpoint(val unsubscribeEndpoint: SubscriptionEndpoint)

                                @Serializable
                                data class SubscriptionEndpoint(
                                    val channelIds: List<String>,
                                    val params: String
                                )

                                @Serializable
                                data class UnsubscribeMessage(
                                    val paidChannelUnsubscribeMessageRenderer: PaidChannelUnsubscribeMessageRenderer
                                ) {
                                    @Serializable
                                    data class PaidChannelUnsubscribeMessageRenderer(
                                        val keepSubscriptionButtonText: ApiText,
                                        val unsubscribeButtonText: ApiText,
                                        val unsubscribeMessage: ApiText,
                                        val unsubscriptionAllowed: Boolean
                                    )
                                }
                            }
                        }
                    }

                    @Serializable
                    data class CompactPlaylistRenderer(
                        val playlistId: String,
                        val thumbnail: ApiThumbnail,
                        val title: ApiText,
                        val videoCountShortText: ApiText,
                        val shortBylineText: ApiText
                    )
                }
            }

            @Serializable
            data class NavigationEndpoint(val browseEndpoint: BrowseEndpoint? = null) {
                @Serializable
                data class BrowseEndpoint(
                    val browseId: String,
                    val canonicalBaseUrl: String
                )
            }

            @Serializable
            data class ThumbnailOverlay(val thumbnailOverlayTimeStatusRenderer: ThumbnailOverlayTimeStatusRenderer) {
                @Serializable
                data class ThumbnailOverlayTimeStatusRenderer(val text: ApiText)
            }
        }
    }

    @Serializable
    data class HeaderRenderer(val elementRenderer: ElementRenderer) {
        @Serializable
        data class ElementRenderer(val newElement: NewElement) {
            @Serializable
            data class NewElement(val type: Type) {
                @Serializable
                data class Type(val componentType: ComponentType) {
                    @Serializable
                    data class ComponentType(val model: Model) {
                        @Serializable
                        data class Model(val shelfHeaderModel: ShelfHeaderModel) {
                            @Serializable
                            data class ShelfHeaderModel(val shelfHeaderData: ShelfHeaderData) {
                                @Serializable
                                data class ShelfHeaderData(val title: String)
                            }
                        }
                    }
                }
            }
        }
    }
}