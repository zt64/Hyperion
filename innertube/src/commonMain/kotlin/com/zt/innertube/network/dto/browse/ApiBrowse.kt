package com.zt.innertube.network.dto.browse

import com.zt.innertube.network.dto.ApiContinuation
import com.zt.innertube.network.dto.renderer.BrowseResultsRenderer
import com.zt.innertube.network.dto.renderer.SectionListRenderer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal abstract class ApiBrowse {
    val continuations: List<ApiContinuation>
        get() = contents.browseResultsRenderer.content.continuations

    abstract val contents: Contents<*>

    @Serializable
    class Contents<T>(
        @SerialName("singleColumnBrowseResultsRenderer")
        val browseResultsRenderer: BrowseResultsRenderer<SectionListRenderer<T>>
    ) {
        val content get() = browseResultsRenderer.content
    }
}

@Serializable
internal abstract class ApiBrowseContinuation {
    val continuations: List<ApiContinuation>
        get() = continuationContents.sectionListContinuation.continuations

    abstract val continuationContents: ContinuationContents<*>

    @Serializable
    class ContinuationContents<T>(val sectionListContinuation: SectionListRenderer<T>) {
        val contents
            get() = sectionListContinuation.contents
    }
}