package com.zt.innertube.domain.model

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

data class Comments(
    override val items: List<DomainComment>,
    override val continuation: String? = null
) : DomainBrowse<DomainComment>()

data class RelatedVideos(
    override val items: List<DomainVideoPartial>,
    override val continuation: String? = null
) : DomainBrowse<DomainVideoPartial>()