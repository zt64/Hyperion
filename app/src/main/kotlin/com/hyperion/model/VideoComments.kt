package com.hyperion.model

data class VideoComments(
    val commentCount: String,
    val comments: List<Comment>,
    val continuation: String,
    val videoId: String
) {
    data class Comment(
        val author: String,
        val authorId: String,
        val authorIsChannelOwner: String,
        val authorThumbnails: List<AuthorThumbnail>,
        val authorUrl: String,
        val commentId: String,
        val content: String,
        val contentHtml: String,
        val creatorHeart: CreatorHeart,
        val isEdited: String,
        val likeCount: String,
        val published: String,
        val publishedText: String,
        val replies: Replies
    ) {
        data class AuthorThumbnail(
            val height: String,
            val url: String,
            val width: String
        )

        data class CreatorHeart(
            val creatorName: String,
            val creatorThumbnail: String
        )

        data class Replies(
            val continuation: String,
            val replyCount: String
        )
    }
}