package com.zt.innertube.domain.model

sealed interface DomainStream {
    val url: String
    val mimeType: String

    data class Video(
        override val url: String,
        override val mimeType: String,
        val itag: Int,
        val label: String
    ) : DomainStream

    data class Audio(
        override val url: String,
        override val mimeType: String,
        val itag: Int
    ) : DomainStream
}