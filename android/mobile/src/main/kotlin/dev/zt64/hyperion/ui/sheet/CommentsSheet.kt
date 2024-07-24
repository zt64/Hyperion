package dev.zt64.hyperion.ui.sheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.resources.MR
import dev.zt64.hyperion.ui.component.ShimmerImage
import dev.zt64.hyperion.ui.tooling.HyperionPreview
import dev.zt64.innertube.domain.model.DomainChannelPartial
import dev.zt64.innertube.domain.model.DomainComment

@Composable
fun CommentsSheet(onDismissRequest: () -> Unit) {
    ModalBottomSheet(onDismissRequest) {
        SheetContent()
    }
}

@Composable
private fun SheetContent() {
    LazyColumn {
        items(20) {
            Comment(
                comment = DomainComment(
                    id = "1",
                    content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                    datePosted = "1 day ago",
                    author = DomainChannelPartial(
                        id = "1",
                        name = "Author name",
                        avatarUrl = "https://i.imgur.com/7bMqy9z.png"
                    ),
                    likeCount = 300,
                    replies = emptyList()
                ),
                onClickLike = {},
                onClickDislike = {},
                onClickReply = {}
            )
        }
    }
}

@Composable
private fun Comment(
    comment: DomainComment,
    onClickLike: () -> Unit,
    onClickDislike: () -> Unit,
    onClickReply: () -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Surface(
        onClick = {}
    ) {
        Column {
            ListItem(
                leadingContent = {
                    ShimmerImage(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape),
                        url = comment.author.avatarUrl!!,
                        contentDescription = null
                    )
                },
                headlineContent = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.weight(1f, true),
                            text = comment.author.name!!,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = comment.datePosted,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                supportingContent = {
                    Text(comment.content)
                }
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClickLike) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = stringResource(MR.strings.like)
                    )
                }

                Text(comment.likeCount.toString())

                IconButton(onClick = onClickDislike) {
                    Icon(
                        imageVector = Icons.Default.ThumbDown,
                        contentDescription = stringResource(MR.strings.dislike)
                    )
                }

                Spacer(Modifier.weight(1f, true))

                if (comment.replies.isNotEmpty()) {
                    Text("${comment.replies.size} replies")
                }

                IconButton(onClick = onClickReply) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.Reply,
                        contentDescription = stringResource(MR.strings.reply)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun CommentCardPreview() {
    HyperionPreview {
        Comment(
            comment = DomainComment(
                id = "1",
                content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                datePosted = "1 day ago",
                author = DomainChannelPartial(
                    id = "1",
                    name = "@author",
                    avatarUrl = "https://i.imgur.com/7bMqy9z.png"
                ),
                likeCount = 300,
                replies = emptyList()
            ),
            onClickLike = {},
            onClickDislike = {},
            onClickReply = {}
        )
    }
}

@Preview
@Composable
private fun CommentsSheetPreview() {
    HyperionPreview {
        SheetContent()
    }
}