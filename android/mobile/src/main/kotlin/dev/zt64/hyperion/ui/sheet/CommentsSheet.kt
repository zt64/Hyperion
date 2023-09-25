 package dev.zt64.hyperion.ui.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.MR
import dev.zt64.innertube.domain.model.DomainComment

@Composable
fun CommentsSheet(onDismissRequest: () -> Unit) {
    ModalBottomSheet(onDismissRequest) {
        LazyColumn(
            modifier = Modifier.padding()
        ) {

        }
    }
}

@Composable
private fun Comment(comment: DomainComment) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = Modifier.combinedClickable(
            onClick = {},
            onLongClick = {}
        )
    ) {
        Column {
            ListItem(
                leadingContent = {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
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
                    // Text(comment.author.name)
                },
                supportingContent = {
                    Text(comment.content)
                }
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = stringResource(MR.strings.like)
                    )
                }

                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Default.ThumbDown,
                        contentDescription = stringResource(MR.strings.dislike)
                    )
                }

                Spacer(Modifier.weight(1f, true))

                Text("${comment.replies.size} replies")

                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.Reply,
                        contentDescription = stringResource(MR.strings.reply)
                    )
                }
            }
        }
    }
}