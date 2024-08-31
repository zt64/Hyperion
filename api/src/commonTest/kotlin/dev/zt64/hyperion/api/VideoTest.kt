package dev.zt64.hyperion.api

import dev.zt64.hyperion.api.util.buildDashManifest
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

// https://www.youtube.com/watch?v=jNQXAC9IVRw
const val VIDEO_ID = "jNQXAC9IVRw"

class VideoTest {
    @Test
    fun testGetVideo() = runTest {
        repo.getVideo(VIDEO_ID)
    }

    @Test
    fun testGetVideoDetails() = runTest {
        val video = repo.getPlayer(VIDEO_ID)

        val manifest = buildDashManifest(video)

        println(manifest)
    }
}