package com.hyperion.model

data class Video(
    val adaptiveFormats: List<AdaptiveFormat>,
    val allowRatings: String,
    val allowedRegions: List<String>,
    val author: String,
    val authorId: String,
    val authorThumbnails: List<AuthorThumbnail>,
    val authorUrl: String,
    val captions: List<Caption>,
    val description: String,
    val descriptionHtml: String,
    val dislikeCount: Int,
    val formatStreams: List<FormatStream>,
    val genre: String,
    val genreUrl: String?,
    val hlsUrl: String,
    val isFamilyFriendly: Boolean,
    val isListed: Boolean,
    val isUpcoming: Boolean,
    val keywords: List<String>,
    val lengthSeconds: Int,
    val likeCount: Int,
    val liveNow: Boolean,
    val paid: Boolean,
    val premiereTimestamp: String,
    val premium: Boolean,
    val published: Int,
    val publishedText: String,
    val rating: String,
    val recommendedVideos: List<RecommendedVideo>,
    val subCountText: String,
    val title: String,
    val videoId: String,
    val videoThumbnails: List<VideoThumbnail>,
    val viewCount: String
) {
    data class AdaptiveFormat(
        val bitrate: String,
        val clen: String,
        val container: String,
        val encoding: String,
        val index: String,
        val `init`: String,
        val itag: String,
        val lmt: String,
        val projectionType: String,
        val qualityLabel: String,
        val resolution: String,
        val type: String,
        val url: String
    )

    data class Caption(
        val label: String,
        val languageCode: String,
        val url: String
    )

    data class FormatStream(
        val container: String,
        val encoding: String,
        val itag: String,
        val quality: String,
        val qualityLabel: String,
        val resolution: String,
        val size: String,
        val type: String,
        val url: String
    )

    data class RecommendedVideo(
        val author: String,
        val lengthSeconds: String,
        val title: String,
        val videoId: String,
        val videoThumbnails: List<VideoThumbnail>,
        val viewCountText: String
    )
}