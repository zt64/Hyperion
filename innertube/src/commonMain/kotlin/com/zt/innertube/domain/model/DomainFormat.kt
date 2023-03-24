package com.zt.innertube.domain.model

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal object FormatSerializer : JsonContentPolymorphicSerializer<DomainFormat>(DomainFormat::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<DomainFormat> {
        val mimeType = element.jsonObject["mimeType"]!!.jsonPrimitive.content

        return when {
            mimeType.startsWith("audio/") -> DomainFormat.Audio.serializer()
            mimeType.startsWith("video/") -> DomainFormat.Video.serializer()
            mimeType.startsWith("text/") -> DomainFormat.Text.serializer()
            else -> throw SerializationException("Unknown format type: $mimeType")
        }
    }
}

@Serializable(with = FormatSerializer::class)
sealed interface DomainFormat {
    val url: String
    val mimeType: String

    @Serializable
    data class Audio(
        override val url: String,
        override val mimeType: String,
        val audioQuality: AudioQuality
    ) : DomainFormat {
        @Serializable
        enum class AudioQuality {
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
        val width: Int,
        val height: Int,
        val qualityLabel: String
    ) : DomainFormat

    @Serializable
    data class Text(
        override val url: String,
        override val mimeType: String,
        val width: Int,
        val height: Int,
        val qualityLabel: String
    ) : DomainFormat
}