package dev.zt64.hyperion.api.domain.model

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.PairSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal object FormatSerializer : JsonContentPolymorphicSerializer<DomainFormat>(
    DomainFormat::class
) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<DomainFormat> {
        val mimeType = element.jsonObject["mimeType"]!!.jsonPrimitive.content

        return when {
            mimeType.startsWith("audio/") -> DomainFormat.Audio.serializer()
            mimeType.startsWith("video/") -> DomainFormat.Video.serializer()
            // mimeType.startsWith("text/") -> DomainFormat.Text.serializer()
            else -> throw SerializationException("Unknown format type: $mimeType")
        }
    }
}

internal object FormatTransformingSerializer :
    JsonTransformingSerializer<DomainFormat>(FormatSerializer) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        return JsonObject(
            element.jsonObject.toMutableMap().apply {
                val (mimeType, rawCodecs) = remove(
                    "mimeType"
                )!!.jsonPrimitive.content.split("; codecs=")

                put("mimeType", JsonPrimitive(mimeType))
                put("codecs", JsonArray(rawCodecs.trim('"').split(",").map(::JsonPrimitive)))
            }
        )
    }
}

@Serializable(with = FormatTransformingSerializer::class)
sealed interface DomainFormat {
    val url: String
    val mimeType: String
    val codecs: List<String>
    val itag: Int
    val approxDurationMs: Long get() = 0
    val initRange: LongRange?
    val indexRange: LongRange?
    val bitrate: Int
    val averageBitrate: Int

    @Serializable
    data class Audio(
        override val url: String,
        override val mimeType: String,
        override val codecs: List<String> = emptyList(),
        override val itag: Int,
        override val approxDurationMs: Long,
        override val initRange: LongRange?,
        override val indexRange: LongRange?,
        override val bitrate: Int,
        override val averageBitrate: Int,
        val audioTrack: JsonElement? = null,
        val audioQuality: AudioQuality,
        val audioSampleRate: Int,
        val audioChannels: Int = 2
    ) : DomainFormat {
        @Serializable
        enum class AudioQuality {
            @SerialName("AUDIO_QUALITY_ULTRALOW")
            ULTRA_LOW,

            @SerialName("AUDIO_QUALITY_LOW")
            LOW,

            @SerialName("AUDIO_QUALITY_MEDIUM")
            MEDIUM,

            @SerialName("AUDIO_QUALITY_HIGH")
            HIGH
        }
    }

    @Serializable
    data class Video(
        override val url: String,
        override val mimeType: String,
        override val codecs: List<String> = emptyList(),
        override val approxDurationMs: Long,
        override val itag: Int,
        override val initRange: LongRange?,
        override val indexRange: LongRange?,
        override val bitrate: Int,
        override val averageBitrate: Int,
        val width: Int,
        val height: Int,
        val fps: Float,
        val qualityLabel: String
    ) : DomainFormat

    // @Serializable
    // data class Text(
    //     override val url: String,
    //     override val mimeType: String,
    //     override val itag: Int,
    //     override val initRange: LongRange?,
    //     override val indexRange: LongRange?,
    //     override val bitrate: Int,
    //     override val averageBitrate: Int,
    //     val width: Int,
    //     val height: Int,
    //     val qualityLabel: String
    // ) : DomainFormat
}

internal typealias LongRange =
    @Serializable(with = RangeSerializer::class)
    ClosedRange<Long>

internal class RangeSerializer<T>(private val tSerializer: KSerializer<T>) :
    KSerializer<ClosedRange<T>> where T : Comparable<T>, T : Number {
    override val descriptor =
        SerialDescriptor("Range", PairSerializer(tSerializer, tSerializer).descriptor)

    override fun deserialize(decoder: Decoder): ClosedRange<T> {
        decoder as JsonDecoder

        val json = decoder.decodeJsonElement().jsonObject

        with(decoder.json) {
            val start = decodeFromJsonElement(tSerializer, json["start"]!!)
            val end = decodeFromJsonElement(tSerializer, json["end"]!!)

            return start..end
        }
    }

    override fun serialize(encoder: Encoder, value: ClosedRange<T>) = error("Not supported")
}