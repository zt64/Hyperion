package com.hyperion.domain.model

open class DomainStream(
    open val url: String
) {
    data class Video(
        override val url: String,
        val itag: Int,
        val label: String
    ) : DomainStream(url)

    data class Audio(
        override val url: String,
        val itag: Int,
        val codec: String
    ) : DomainStream(url)
}