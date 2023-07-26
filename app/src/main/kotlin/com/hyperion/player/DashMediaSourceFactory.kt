@file:UnstableApi

package com.hyperion.player

import android.net.Uri
import androidx.media3.common.C
import androidx.media3.common.Format
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.dash.manifest.*
import androidx.media3.exoplayer.drm.DefaultDrmSessionManagerProvider
import androidx.media3.exoplayer.drm.DrmSessionManagerProvider
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.upstream.DefaultLoadErrorHandlingPolicy
import androidx.media3.exoplayer.upstream.LoadErrorHandlingPolicy
import com.zt.innertube.domain.model.DomainFormat
import com.zt.innertube.domain.repository.InnerTubeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

@UnstableApi
class DashMediaSourceFactory(
    val dataSourceFactory: DataSource.Factory,
    private val repository: InnerTubeRepository
) : MediaSource.Factory {
    private var drmSessionManagerProvider: DrmSessionManagerProvider =
        DefaultDrmSessionManagerProvider()
    private var loadErrorHandlingPolicy: LoadErrorHandlingPolicy = DefaultLoadErrorHandlingPolicy()

    private val factory = DashMediaSource.Factory(dataSourceFactory)
        .setLoadErrorHandlingPolicy(loadErrorHandlingPolicy)
        .setDrmSessionManagerProvider(drmSessionManagerProvider)

    override fun setLoadErrorHandlingPolicy(loadErrorHandlingPolicy: LoadErrorHandlingPolicy) =
        apply {
            this.loadErrorHandlingPolicy = loadErrorHandlingPolicy
        }

    override fun setDrmSessionManagerProvider(drmSessionManagerProvider: DrmSessionManagerProvider) =
        apply {
            this.drmSessionManagerProvider = drmSessionManagerProvider
        }

    override fun createMediaSource(mediaItem: MediaItem): MediaSource {
        val res = runBlocking(Dispatchers.IO) {
            repository.getVideo(mediaItem.mediaId)
        }

        val cpn = generateCpn()
        val groups = res.formats.groupBy(DomainFormat::mimeType)
        val sets = groups.entries.mapIndexed { index, (_, items) ->
            val audioTrack = (items.first() as? DomainFormat.Audio)?.audioTrack

            if (audioTrack != null) {
                AdaptationSet(
                    /* id= */ index,
                    /* type = */ C.TRACK_TYPE_DEFAULT,
                    /* representations = */ items.map { it.generateRepresentation(cpn) },
                    /* accessibilityDescriptors = */ emptyList(),
                    /* essentialProperties = */ emptyList(),
                    /* supplementalProperties = */ emptyList(),
                )
            } else {
                AdaptationSet(
                    /* id= */ index,
                    /* type = */ C.TRACK_TYPE_DEFAULT,
                    /* representations = */ items.map { it.generateRepresentation(cpn) },
                    /* accessibilityDescriptors = */ emptyList(),
                    /* essentialProperties = */ emptyList(),
                    /* supplementalProperties = */ emptyList(),
                )
            }
        }

        return factory.createMediaSource(
            DashManifest(
                /* availabilityStartTimeMs = */ C.TIME_UNSET,
                /* durationMs = */ res.formats.first().approxDurationMs,
                /* minBufferTimeMs = */ MIN_BUFFER_TIME,
                /* dynamic = */ false,
                /* minUpdatePeriodMs = */ C.TIME_UNSET,
                /* timeShiftBufferDepthMs = */ C.TIME_UNSET,
                /* suggestedPresentationDelayMs = */ C.TIME_UNSET,
                /* publishTimeMs = */ C.TIME_UNSET,
                /* programInformation = */ null,
                /* utcTiming = */ null,
                /* serviceDescription = */ null,
                /* location = */ null,
                /* periods = */ listOf(Period(null, 0, sets))
            )
        )
    }

    override fun getSupportedTypes() = intArrayOf(C.CONTENT_TYPE_DASH)

    private fun generateCpn(): String {
        val chars = ('A'..'Z').toList()

        return List(CPN_LENGTH) { chars.random() }.joinToString("")
    }

    private fun DomainFormat.generateRepresentation(cpn: String) = generateRepresentation(
        formatBuilder = Format.Builder().apply {
            when (this@generateRepresentation) {
                is DomainFormat.Video -> {
                    setWidth(width)
                    setHeight(height)
                    setFrameRate(fps)
                }

                is DomainFormat.Audio -> {
                    setChannelCount(audioChannels)
                    setSampleRate(audioSampleRate)
                }

                else -> throw IllegalArgumentException("Unknown format type: $this")
            }
        },
        cpn = cpn
    )

    private fun DomainFormat.generateRepresentation(
        formatBuilder: Format.Builder,
        cpn: String
    ): Representation {
        // "video/webm; codecs=\"vp9\""
        val (mimeType, end) = mimeType.split("; codecs=\"")
        val codecs = end.dropLast(1)

        val uri = Uri.Builder()
            .path(url)
            .appendQueryParameter("cpn", cpn)
            .build()
            .toString()

        val segmentBase = SegmentBase.SingleSegmentBase(
            /* initialization = */ RangedUri(
                null,
                initRange.start,
                initRange.endInclusive
            ),
            /* timescale = */ 1,
            /* presentationTimeOffset = */ 0,
            /* indexStart = */ indexRange.start,
            /* indexLength = */ indexRange.endInclusive
        )

        return Representation.newInstance(
            /* revisionId = */ Representation.REVISION_ID_DEFAULT,
            /* format = */ formatBuilder
                .setId(itag)
                .setCodecs(codecs)
                .setAverageBitrate(averageBitrate)
                .setPeakBitrate(bitrate)
                .setSampleMimeType(mimeType)
                .build(),
            /* baseUrls = */ listOf(BaseUrl(uri)),
            /* segmentBase = */ segmentBase
        )
    }

    private companion object {
        private const val MIN_BUFFER_TIME = 1500L
        private const val CPN_LENGTH = 16
    }
}