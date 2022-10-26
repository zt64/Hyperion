package com.hyperion.network.dto

import com.hyperion.network.dto.renderer.ElementRenderer
import com.hyperion.network.dto.renderer.ItemSectionRenderer
import kotlinx.serialization.Serializable

@Serializable
internal data class ApiTrending(
    override val contents: Contents<SectionContent>
) : ApiBrowse() {
    @Serializable
    data class SectionContent(val itemSectionRenderer: ItemSectionRenderer<Content>) {
        @Serializable
        data class Content(val elementRenderer: ElementRenderer<Model>) {
            @Serializable
            data class Model(val videoWithContextModel: ApiVideo? = null)
        }
    }
}

@Serializable
internal class ApiTrendingContinuation(
    override val continuationContents: ContinuationContents<ApiTrending.SectionContent>
) : ApiBrowseContinuation()