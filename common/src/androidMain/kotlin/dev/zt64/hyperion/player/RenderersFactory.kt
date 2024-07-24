package dev.zt64.hyperion.player

import android.content.Context
import android.os.Handler
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.Renderer
import androidx.media3.exoplayer.RenderersFactory
import androidx.media3.exoplayer.audio.AudioRendererEventListener
import androidx.media3.exoplayer.audio.MediaCodecAudioRenderer
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
import androidx.media3.exoplayer.metadata.MetadataOutput
import androidx.media3.exoplayer.text.TextOutput
import androidx.media3.exoplayer.video.MediaCodecVideoRenderer
import androidx.media3.exoplayer.video.VideoRendererEventListener

@UnstableApi
internal class RenderersFactory(private val context: Context) : RenderersFactory {
    override fun createRenderers(
        handler: Handler,
        videoListener: VideoRendererEventListener,
        audioListener: AudioRendererEventListener,
        textRendererOutput: TextOutput,
        metadataRendererOutput: MetadataOutput
    ): Array<Renderer> {
        var renderers = arrayOf<Renderer>()
        val codecSelector = MediaCodecSelector.DEFAULT

        renderers += MediaCodecAudioRenderer(
            // context =
            context,
            // mediaCodecSelector =
            codecSelector,
            // eventHandler =
            handler,
            // eventListener =
            audioListener
        )

        renderers += MediaCodecVideoRenderer(
            // context =
            context,
            // mediaCodecSelector =
            codecSelector,
            // allowedJoiningTimeMs =
            5000,
            // enableDecoderFallback =
            true,
            // eventHandler =
            handler,
            // eventListener =
            videoListener,
            // maxDroppedFramesToNotify =
            50
        )

        return renderers
    }
}