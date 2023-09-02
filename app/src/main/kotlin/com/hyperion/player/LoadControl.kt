package com.hyperion.player

import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.upstream.DefaultAllocator

@UnstableApi
fun buildLoadControl() = DefaultLoadControl.Builder()
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