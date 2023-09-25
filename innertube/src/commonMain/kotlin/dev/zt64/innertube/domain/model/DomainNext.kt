package dev.zt64.innertube.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class DomainNext(
    val viewCount: String,
    val uploadDate: String,
    val channelAvatarUrl: String,
    val likesText: String,
    val subscribersText: String? = null,
    val comments: Comments,
    val relatedVideos: RelatedVideos,
    val badges: List<String>,
    val chapters: List<DomainChapter>
)

typealias Comments = DomainBrowse<DomainComment>

typealias RelatedVideos = DomainBrowse<DomainVideoPartial>