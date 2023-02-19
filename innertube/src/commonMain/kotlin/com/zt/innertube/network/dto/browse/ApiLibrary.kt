package com.zt.innertube.network.dto.browse

import com.zt.innertube.network.dto.ApiImage
import com.zt.innertube.network.dto.ApiText
import com.zt.innertube.network.dto.renderer.ElementRenderer
import com.zt.innertube.network.dto.renderer.ItemSectionRenderer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
internal data class ApiLibrary(
    override val contents: Contents<SectionContent>
) : ApiBrowse() {
    @Serializable
    data class SectionContent(
        val itemSectionRenderer: ItemSectionRenderer<Content>? = null,
        val clientSortingSectionRenderer: JsonObject? = null
    ) {
        @Serializable
        data class Content(
            val elementRenderer: ElementRenderer<Model>? = null,
            val clientSortingSectionRenderer: ClientSortingSectionRenderer? = null
        ) {
            @Serializable
            data class Model(val libraryRecentShelfModel: LibraryRecentShelfModel)

            @Serializable
            data class ClientSortingSectionRenderer(val contents: List<ListItemWrapper>)
        }

        @Serializable
        data class ListItemWrapper(val compactListItemRenderer: CompactListItemRenderer)
    }

    @Serializable
    data class CompactListItemRenderer(
        val title: ApiText,
        @SerialName("subTitle")
        val subtitle: ApiText,
        val thumbnail: ListItemThumbnail
    ) {
        @Serializable
        data class ListItemThumbnail(val playlistCroppedThumbnailRenderer: PlaylistCroppedThumbnailRenderer) {
            @Serializable
            data class PlaylistCroppedThumbnailRenderer(val thumbnail: ApiImage)
        }
    }

    @Serializable
    data class LibraryRecentShelfModel(val `data`: Data) {
        @Serializable
        data class Data(val cards: List<Card>) {
            @Serializable
            data class Card(val videoCard: VideoCard) {
                @Serializable
                data class VideoCard(val videoData: VideoData)
            }
        }

        @Serializable
        data class VideoData(
            val isLargeFormFactor: Boolean,
            val metadata: Metadata,
            val thumbnail: Thumbnail,
            val videoId: String
        ) {
            @Serializable
            data class Metadata(
                val byline: String,
                val isLargeFormFactor: Boolean,
                val isVideoCard: Boolean,
                val title: String
            )

            @Serializable
            data class Thumbnail(
                val image: ApiImage,
                val isVideoCard: Boolean,
                val percentDurationWatched: Double,
                val timestampText: String,
                val triptych: Triptych
            ) {
                @Serializable
                data class Triptych(val images: List<ApiImage>)
            }
        }
    }
}