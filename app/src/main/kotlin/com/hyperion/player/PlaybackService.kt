package com.hyperion.player

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.upstream.DefaultAllocator
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSession.ConnectionResult
import androidx.media3.session.MediaSessionService
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.hyperion.ui.MainActivity

@UnstableApi
class PlaybackService : MediaSessionService() {
    private lateinit var mediaSession: MediaSession
    private lateinit var player: ExoPlayer

    override fun onCreate() {
        super.onCreate()

        player = ExoPlayer.Builder(application)
            .setTrackSelector(DefaultTrackSelector(application))
            .setAudioAttributes(
                /* audioAttributes = */ AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                    .build(),
                /* handleAudioFocus = */ true
            )
            .setRenderersFactory(
                DefaultRenderersFactory(application)
                    .setEnableAudioTrackPlaybackParams(true)
                    .setEnableAudioOffload(true)
                    .forceEnableMediaCodecAsynchronousQueueing()
                    .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
                    .setEnableDecoderFallback(true)
            )
            .setMediaSourceFactory(ProgressiveMediaSource.Factory(DefaultHttpDataSource.Factory()))
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

        player.playWhenReady = true
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaSession

    override fun onDestroy() {
        player.release()
        mediaSession.release()

        super.onDestroy()
    }

    private inner class MediaSessionCallback : MediaSession.Callback {
        override fun onConnect(session: MediaSession, controller: MediaSession.ControllerInfo): ConnectionResult {
            val connectionResult = super.onConnect(session, controller)
            val sessionCommands = connectionResult.availableSessionCommands
            val playerCommands = connectionResult.availablePlayerCommands

            return ConnectionResult.accept(sessionCommands, playerCommands)
        }

        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: MutableList<MediaItem>
        ): ListenableFuture<MutableList<MediaItem>> {
            val updatedMediaItems = mediaItems.map {
                it.buildUpon()
                    .setUri(it.mediaId)
                    .build()
            }.toMutableList()

            return Futures.immediateFuture(updatedMediaItems)
        }
    }
}