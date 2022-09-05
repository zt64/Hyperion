package com.hyperion.network.dto

import com.hyperion.network.dto.renderer.ElementRenderer
import com.hyperion.network.dto.renderer.ItemSectionRenderer
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@Serializable
data class ApiNext(val contents: Contents) {
    @Serializable(with = Renderer.Serializer::class)
    sealed interface Renderer {
        companion object Serializer : KSerializer<Renderer> {
            override val descriptor = buildClassSerialDescriptor("Renderer")

            override fun deserialize(decoder: Decoder): Renderer {
                val input = decoder as JsonDecoder
                val tree = input.decodeJsonElement().jsonObject
                val json = input.json

                val serializer = when (tree.keys.single()) {
                    "slimVideoMetadataSectionRenderer" -> SlimVideoMetadataSection.serializer()
                    "itemSectionRenderer" -> ItemSection.serializer()
                    "shelfRenderer" -> Shelf.serializer()
                    else -> throw NoWhenBranchMatchedException()
                }

                return json.decodeFromJsonElement(serializer, tree.values.single())
            }

            override fun serialize(encoder: Encoder, value: Renderer) {
                TODO("Not yet implemented")
            }
        }

        @Serializable
        data class SlimVideoMetadataSection(val contents: List<Content>) : Renderer {
            @Serializable
            data class Content(val elementRenderer: ElementRenderer<Model>)
        }

        @Serializable
        object Shelf : Renderer

        @Serializable(with = ItemSection.Serializer::class)
        sealed interface ItemSection : Renderer {
            companion object Serializer :
                JsonContentPolymorphicSerializer<ItemSection>(ItemSection::class) {
                override fun selectDeserializer(
                    element: JsonElement
                ): DeserializationStrategy<out ItemSection> {
                    val sectionIdentifier = element.jsonObject["sectionIdentifier"]
                        ?.jsonPrimitive
                        ?.contentOrNull

                    return when (sectionIdentifier) {
                        "comments-entry-point" -> CommentsEntry.serializer()
                        "related-items" -> RelatedItems.serializer()
                        null -> Separator.serializer()
                        else -> throw NoWhenBranchMatchedException()
                    }
                }
            }

            @Serializable
            data class CommentsEntry(val contents: List<Content>) : ItemSection {
                @Serializable
                data class Content(val elementRenderer: ElementRenderer<Model>)

                @Serializable
                object Model
            }

            @Serializable
            data class RelatedItems(val contents: List<Content>) : ItemSection {
                @Serializable
                data class Content(val elementRenderer: ElementRenderer<Model>)

                @Serializable
                data class Model(val videoWithContextModel: ApiNextVideo? = null)
            }

            @Serializable
            object Separator : ItemSection
        }
    }

    @Serializable
    data class Contents(val singleColumnWatchNextResults: SingleColumnWatchNextResults) {
        @Serializable
        data class SingleColumnWatchNextResults(val results: Results) {
            @Serializable
            data class Results(val results: Results) {
                @Serializable
                data class Results(
                    val contents: List<Renderer> = emptyList(),
                    val continuations: List<ApiContinuation> = emptyList()
                )
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
                val title: Text,
                val subtitleData: SubtitleData,
                val badgesData: List<Badge> = emptyList()
            ) {
                @Serializable
                data class Text(val content: String)

                @Serializable
                data class SubtitleData(
                    val viewCount: Text,
                    val date: Text
                )

                @Serializable
                data class Badge(val label: String)
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
                data class Avatar(val image: ApiImage)
            }
        }

        @Serializable
        data class VideoActionBarModel(val buttons: List<Button>)
    }

    @Serializable(with = Button.Serializer::class)
    sealed interface Button {
        companion object Serializer : KSerializer<Button> {
            override val descriptor = buildClassSerialDescriptor("Button")

            override fun deserialize(decoder: Decoder): Button {
                val input = decoder as JsonDecoder
                val tree = input.decodeJsonElement().jsonObject
                val json = input.json

                return when (tree.keys.single()) {
                    "likeButton" -> json.decodeFromJsonElement(LikeButton.serializer(), tree["likeButton"]!!)
                    "dislikeButton" -> json.decodeFromJsonElement(DislikeButton.serializer(), tree["dislikeButton"]!!)
                    else -> OtherButton
                }
            }

            override fun serialize(encoder: Encoder, value: Button) = TODO("Not yet implemented")
        }

        @Serializable
        data class ButtonData(val defaultButton: DefaultButton) {
            @Serializable
            data class DefaultButton(val title: String)
        }

        @Serializable
        data class LikeButton(val buttonData: ButtonData) : Button

        @Serializable
        data class DislikeButton(val buttonData: ButtonData) : Button

        @Serializable
        object OtherButton : Button
    }
}

@Serializable
data class ApiNextContinuation(val continuationContents: ContinuationContents) {
    @Serializable
    data class ContinuationContents(val sectionListContinuation: SectionListContinuation) {
        @Serializable
        data class SectionListContinuation(
            val contents: List<Content>,
            val continuations: List<ApiContinuation>
        )

        @Serializable
        data class Content(val itemSectionRenderer: ItemSectionRenderer<Content>) {
            @Serializable
            data class Content(val elementRenderer: ElementRenderer<Model>)
        }
    }

    @Serializable
    data class Model(val videoWithContextModel: ApiNextVideo? = null)
}