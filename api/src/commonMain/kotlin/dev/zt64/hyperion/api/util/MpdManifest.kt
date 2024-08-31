package dev.zt64.hyperion.api.util

import dev.zt64.hyperion.api.domain.model.DomainFormat
import dev.zt64.hyperion.api.domain.model.DomainStreamingData
import dev.zt64.hyperion.api.domain.model.LongRange
import kotlin.math.abs
import kotlin.math.roundToInt

private const val DASH_NAMESPACE = "urn:mpeg:dash:schema:mpd:2011"
private const val DASH_PROFILE = "urn:mpeg:dash:profile:full:2011"
private const val DASH_MIN_BUFFER_TIME = "PT1.5S"
private const val DASH_TYPE = "static"

fun buildDashManifest(streamingData: DomainStreamingData): String {
    val uniqueRes = false
    val audioStreams = streamingData.formats.filterIsInstance<DomainFormat.Audio>().sortedBy {
        it.bitrate
    }.reversed()
    val videoStreams = streamingData.formats.filterIsInstance<DomainFormat.Video>()
        .sortedWith(
            compareByDescending<DomainFormat.Video> { it.width }
                .thenByDescending { it.fps }
        )

    return buildManifest {
        element(
            "MPD",
            "xmlns" to DASH_NAMESPACE,
            "profiles" to DASH_PROFILE,
            "minBufferTime" to DASH_MIN_BUFFER_TIME,
            "type" to DASH_TYPE,
            "mediaPresentationDuration" to "PT${streamingData.lengthSeconds}S"
        ) {
            element("Period") {
                var i = 0

                for (format in audioStreams.filter { it.mimeType == "audio/mp4" }) {
                    if (format.indexRange == null || format.initRange == null) continue

                    element(
                        "AdaptationSet",
                        "id" to i.toString(),
                        "mimeType" to format.mimeType,
                        "startWithSAP" to "1",
                        "subsegmentAlignment" to "true",
                        "label" to "${format.bitrate}k"
                    ) {
                        val codecs = format.codecs

                        element(
                            "Role",
                            "schemeIdUri" to "urn:mpeg:dash:role:2011",
                            "value" to if (i == 0) "main" else "alternate"
                        )

                        element(
                            "Representation",
                            "id" to format.itag.toString(),
                            "codecs" to format.codecs.joinToString(","),
                            "bandwidth" to format.bitrate.toString()
                        ) {
                            element(
                                "AudioChannelConfiguration",
                                "schemeIdUri" to
                                    "urn:mpeg:dash:23003:3:audio_channel_configuration:2011",
                                "value" to "2"
                            )

                            baseUrl(format.url)
                            segmentBase(format.indexRange, format.initRange)
                        }
                    }

                    i++
                }

                val potentialHeights = listOf(4320, 2160, 1440, 1080, 720, 480, 360, 240, 144)

                for (mimeType in listOf("video/mp4")) {
                    val mimeStreams = videoStreams.filter { it.mimeType == mimeType }
                    val heights = mutableListOf<Int>()

                    element(
                        "AdaptationSet",
                        "id" to i.toString(),
                        "mimeType" to mimeType,
                        "startWithSAP" to "1",
                        "subsegmentAlignment" to "true",
                        "scanType" to "progressive"
                    ) {
                        for (format in mimeStreams) {
                            if (format.indexRange == null || format.initRange == null) continue

                            val height = potentialHeights.minBy { abs(format.height - it) }
                            if (uniqueRes && height in heights) continue
                            heights += height

                            element(
                                "Representation",
                                "id" to format.itag.toString(),
                                "codecs" to format.codecs.joinToString(","),
                                "width" to format.width.toString(),
                                "height" to format.height.toString(),
                                "startWithSAP" to "1",
                                "maxPlayoutRate" to "1",
                                "bandwidth" to format.bitrate.toString(),
                                "frameRate" to format.fps.roundToInt().toString()
                            ) {
                                baseUrl(format.url)
                                segmentBase(format.indexRange, format.initRange)
                            }
                        }
                    }

                    i++
                }
            }
        }
    }
}

class MpdManifestBuilder {
    private val stringBuilder = StringBuilder()

    init {
        stringBuilder.appendLine("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
    }

    fun build(): String {
        return stringBuilder.toString()
    }

    fun element(name: String, vararg attributes: Pair<String, String>, block: (MpdManifestBuilder.() -> Unit)? = null) {
        element(name, attributes.toMap(), block)
    }

    fun element(name: String, attributes: Map<String, String> = emptyMap(), block: (MpdManifestBuilder.() -> Unit)? = null) {
        stringBuilder.append("<$name")
        attributes.forEach { (key, value) ->
            stringBuilder.append(" $key=\"$value\"")
        }
        if (block == null) {
            stringBuilder.appendLine("/>")
            return
        } else {
            stringBuilder.appendLine(">")
            block()
            stringBuilder.appendLine("</$name>")
        }
    }

    fun text(text: String) {
        stringBuilder.append(
            text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;")
        )
    }

    fun baseUrl(url: String) {
        element("BaseURL") {
            text(url)
        }
    }

    fun segmentBase(indexRange: LongRange, initRange: LongRange) {
        element(
            "SegmentBase",
            "indexRange" to "${indexRange.start}-${indexRange.endInclusive}"
        ) {
            element(
                "Initialization",
                "range" to "${initRange.start}-${initRange.endInclusive}"
            )
        }
    }
}

fun buildManifest(block: MpdManifestBuilder.() -> Unit): String {
    return MpdManifestBuilder().apply(block).build()
}