package com.zt.innertube.network.dto.browse

import kotlinx.serialization.Serializable

@Serializable
internal data class ApiTrending(
    override val contents: Contents<SectionContent>
) : ApiBrowse() {
    @Serializable
    object SectionContent
}

@Serializable
internal data class ApiTrendingContinuation(
    override val onResponseReceivedActions: List<ContinuationContents<ApiTrending.SectionContent>>
) : ApiBrowseContinuation()