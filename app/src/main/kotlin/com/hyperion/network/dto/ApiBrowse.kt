package com.hyperion.network.dto

import com.hyperion.network.dto.renderer.BrowseResultsRenderer
import com.hyperion.network.dto.renderer.SectionListRenderer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
abstract class ApiBrowse {
    abstract val contents: Contents<*>

    @Serializable
    class Contents<T>(
        @SerialName("singleColumnBrowseResultsRenderer")
        val browseResultsRenderer: BrowseResultsRenderer<SectionListRenderer<T>>
    )
}

@Serializable
abstract class ApiBrowseContinuation {
    abstract val continuationContents: ContinuationContents<*>

    @Serializable
    class ContinuationContents<T>(val sectionListContinuation: SectionListRenderer<T>)
}