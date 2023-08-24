@file:UnstableApi

package com.hyperion.player

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.common.*
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.*
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.dash.manifest.DashManifest
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.upstream.DefaultAllocator
import androidx.media3.exoplayer.util.EventLogger
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSession.ControllerInfo
import androidx.media3.session.MediaSessionService
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.hyperion.ui.MainActivity
import com.zt.innertube.domain.repository.InnerTubeRepository
import org.koin.android.ext.android.inject

class PlaybackService : MediaSessionService() {
    private val innerTube: InnerTubeRepository by inject()

    private lateinit var cache: Cache
    private lateinit var mediaSession: MediaSession
    private lateinit var player: ExoPlayer

    override fun onCreate() {
        super.onCreate()

        val params = DefaultTrackSelector.Parameters.getDefaults(application)
            .buildUpon()
            .clearVideoSizeConstraints()
            .clearViewportSizeConstraints()
            .build()

        val trackSelectorFactory = AdaptiveTrackSelection.Factory()
        val trackSelector = DefaultTrackSelector(application, params, trackSelectorFactory)

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
            .build()

        cache = SimpleCache(
            /* cacheDir = */ application.cacheDir.resolve("video"),
            /* evictor = */ LeastRecentlyUsedCacheEvictor(CACHE_SIZE),
            /* databaseProvider = */ StandaloneDatabaseProvider(application),
        )

        player = ExoPlayer.Builder(application)
            .setTrackSelector(trackSelector)
            .setAudioAttributes(audioAttributes, true)
            .setRenderersFactory(
                DefaultRenderersFactory(application)
                    .setEnableAudioTrackPlaybackParams(true)
                    .forceEnableMediaCodecAsynchronousQueueing()
                    .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
                    .setEnableDecoderFallback(true)
            )
            .setMediaSourceFactory(
                DashMediaSourceFactory(
                    dataSourceFactory = CacheDataSource.Factory()
                        .setCache(cache)
                        .setCacheWriteDataSinkFactory(
                            CacheDataSink.Factory().setCache(cache)
                        )
                        .setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory())
                        .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR),
                    repository = innerTube
                )
            )
            .setUseLazyPreparation(true)
            .setLoadControl(
                DefaultLoadControl.Builder()
                    .setBufferDurationsMs(
                        /* minBufferMs= */ 2000,
                        /* maxBufferMs= */ 5000,
                        /* bufferForPlaybackMs= */ 1500,
                        /* bufferForPlaybackAfterRebufferMs= */ 2000
                    )
                    .setPrioritizeTimeOverSizeThresholds(true)
                    .setTargetBufferBytes(C.LENGTH_UNSET)
                    .setBackBuffer(
                        /* backBufferDurationMs= */ 10000,
                        /* retainBackBufferFromKeyframe= */ false
                    )
                    .setAllocator(DefaultAllocator(true, 16))
                    .build()
            )
            .setWakeMode(C.WAKE_MODE_NETWORK)
            .setHandleAudioBecomingNoisy(true)
            .setSeekBackIncrementMs(15000)
            .setSeekForwardIncrementMs(15000)
            .setPauseAtEndOfMediaItems(false)
            .setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
            .build()

        mediaSession = MediaSession.Builder(application, player)
            .setCallback(MediaSessionCallback())
            .setSessionActivity(
                PendingIntent.getActivity(
                    /* context = */ application,
                    /* requestCode = */ 0,
                    /* intent = */ Intent(application, MainActivity::class.java),
                    /* flags = */ PendingIntent.FLAG_IMMUTABLE
                )
            )
            .build()

        if (BuildConfig.DEBUG) {
            player.addAnalyticsListener(EventLogger())

            player.addListener(
                object : Player.Listener {
                    override fun onTimelineChanged(
                        timeline: Timeline,
                        @Player.TimelineChangeReason
                        reason: Int
                    ) {
                        val manifest = player.currentManifest as DashManifest? ?: return

                        println(manifest)
                    }
                }
            )
        }

        player.playWhenReady = true
    }

    override fun onGetSession(controllerInfo: ControllerInfo) = mediaSession

    override fun onDestroy() {
        player.release()
        mediaSession.release()
        cache.release()

        super.onDestroy()
    }

    private inner class MediaSessionCallback : MediaSession.Callback {
        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: ControllerInfo,
            mediaItems: List<MediaItem>
        ): ListenableFuture<List<MediaItem>> {
            return Futures.immediateFuture(mediaItems)
        }
    }

    private companion object {
        const val CACHE_SIZE: Long = 200 * 1024 * 1024
    }
}