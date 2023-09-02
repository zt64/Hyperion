package com.hyperion.player

import androidx.media3.common.util.UnstableApi
import androidx.media3.extractor.Extractor
import androidx.media3.extractor.ExtractorsFactory
import androidx.media3.extractor.mkv.MatroskaExtractor
import androidx.media3.extractor.mp4.FragmentedMp4Extractor
import androidx.media3.extractor.mp4.Mp4Extractor

@UnstableApi
class ExtractorsFactory : ExtractorsFactory {
    override fun createExtractors(): Array<Extractor> = arrayOf(
        Mp4Extractor(),
        MatroskaExtractor(),
        FragmentedMp4Extractor()
    )
}