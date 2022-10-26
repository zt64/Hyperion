package com.hyperion.network.dto

import com.hyperion.network.dto.renderer.ElementRenderer
import com.hyperion.network.dto.renderer.ItemSectionRenderer
import kotlinx.serialization.Serializable

@Serializable
internal data class ApiRecommended(
    override val contents: Contents<SectionContent>
) : ApiBrowse() {
    @Serializable
    data class SectionContent(val itemSectionRenderer: ItemSectionRenderer<Content>) {
        @Serializable
        data class Content(val elementRenderer: ElementRenderer<Model>? = null) {
            @Serializable
            data class Model(val videoWithContextModel: ApiVideo? = null)
        }
    }
}

@Serializable
internal data class ApiRecommendedContinuation(
    override val continuationContents: ContinuationContents<ApiRecommended.SectionContent>
) : ApiBrowseContinuation()