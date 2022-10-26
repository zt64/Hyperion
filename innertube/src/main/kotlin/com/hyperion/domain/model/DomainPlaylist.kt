package com.hyperion.domain.model

data class DomainPlaylist(
    val id: String,
    val name: String,
    val videoCount: String,
    val channel: DomainChannelPartial,
    override val items: List<DomainVideoPartial>,
    override val continuation: String?
) : DomainBrowse<DomainVideoPartial>() {
    val shareUrl = "https://youtube.com/playlist?list=$id"
}

data class DomainPlaylistPartial(
    val id: String,
    val title: String,
    val subtitle: String,
    val videoCountText: String,
    val thumbnailUrl: String
) : Entity