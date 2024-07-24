package dev.zt64.hyperion.player

import androidx.media3.common.util.UnstableApi
import androidx.media3.extractor.Extractor
import androidx.media3.extractor.ExtractorsFactory
import androidx.media3.extractor.mkv.MatroskaExtractor
import androidx.media3.extractor.mp4.FragmentedMp4Extractor
import androidx.media3.extractor.mp4.Mp4Extractor
import androidx.media3.extractor.text.DefaultSubtitleParserFactory

@UnstableApi
internal class ExtractorsFactory : ExtractorsFactory {
    override fun createExtractors(): Array<Extractor> {
        val subtitleParserFactory = DefaultSubtitleParserFactory()

        return arrayOf(
            Mp4Extractor(subtitleParserFactory),
            MatroskaExtractor(subtitleParserFactory),
            FragmentedMp4Extractor(subtitleParserFactory)
        )
    }
}