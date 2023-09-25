package dev.zt64.hyperion.domain.model.channel

import androidx.compose.runtime.Immutable
import dev.icerock.moko.resources.StringResource
import dev.zt64.hyperion.MR

@Immutable
enum class VideoSort(val displayName: StringResource) {
    RECENTLY_UPLOADED(MR.strings.recently_uploaded),
    POPULAR(MR.strings.popular)
}