@file:UnstableApi

package com.hyperion.player

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.util.EventLogger
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSession.ControllerInfo
import androidx.media3.session.MediaSessionService
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.hyperion.BuildConfig
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

        player = ExoPlayer.Builder(
            /* context = */ application,
            /* renderersFactory = */ RenderersFactory(application),
            /* mediaSourceFactory = */ buildMediaSourceFactory(cache, innerTube)
        ).apply {
            setTrackSelector(trackSelector)
            setAudioAttributes(audioAttributes, true)
            setUseLazyPreparation(true)
            setLoadControl(buildLoadControl())
            setWakeMode(C.WAKE_MODE_NETWORK)
            setHandleAudioBecomingNoisy(true)
            setSeekBackIncrementMs(15000)
            setSeekForwardIncrementMs(15000)
            setPauseAtEndOfMediaItems(false)
            setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
        }.build()

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