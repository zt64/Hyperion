package com.hyperion.network.dto

import com.hyperion.network.dto.renderer.ElementRenderer
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
                        val longBylineText: ApiBylineText,
                        val navigationEndpoint: NavigationEndpoint,
                        val publishedTimeText: ApiText? = null,
                        val shortBylineText: ApiBylineText,
                        val shortViewCountText: ApiText? = null,
                        val thumbnail: ApiThumbnail,
                        val thumbnailOverlays: List<ApiThumbnailOverlay>,
                        val title: ApiText,
                        val videoId: String,
                        val viewCountText: ApiText? = null
                    ) {
                        @Serializable
                        data class Badge(val textBadge: TextBadge) {
                            @Serializable
                            data class TextBadge(val label: ApiText)
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
                                data class OnSubscribeEndpoint(val subscribeEndpoint: ApiSubscriptionEndpoint)

                                @Serializable
                                data class OnUnsubscribeEndpoint(val unsubscribeEndpoint: ApiSubscriptionEndpoint)

                                @Serializable
                                data class UnsubscribeMessage(
                                    val paidChannelUnsubscribeMessageRenderer: UnsubscribeMessageRenderer
                                ) {
                                    @Serializable
                                    data class UnsubscribeMessageRenderer(
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
            data class NavigationEndpoint(val browseEndpoint: ApiBrowseEndpoint? = null)
        }
    }

    @Serializable
    data class HeaderRenderer(val elementRenderer: ElementRenderer<Model>) {
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