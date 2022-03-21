package com.hyperion.model

data class VideoComments(
    val commentCount: Int?,
    val comments: List<Comment>,
    val continuation: String?,
    val videoId: String
) {
    data class Comment(
        val author: String,
        val authorId: String,
        val authorIsChannelOwner: Boolean,
        val authorThumbnails: List<AuthorThumbnail>,
        val authorUrl: String,
        val commentId: String,
        val content: String,
        val contentHtml: String,
        val creatorHeart: CreatorHeart,
        val isEdited: Boolean,
        val likeCount: Int,
        val published: Long,
        val publishedText: String,
        val replies: Replies
    ) {
        data class CreatorHeart(
            val creatorName: String,
            val creatorThumbnail: String
        )

        data class Replies(
            val continuation: String,
            val replyCount: Int
        )
    }
}