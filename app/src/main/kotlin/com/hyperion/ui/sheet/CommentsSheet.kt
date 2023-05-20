package com.hyperion.ui.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CommentsSheet(
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(onDismissRequest) {
        LazyColumn(
            modifier = Modifier.padding()
        ) {

        }
    }
}

@Preview
@Composable
private fun Comment() {
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
                            text = "Comment author",
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = "1 hour ago",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    //                Text(comment.author.name)
                },
                supportingContent = {
                    Text("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Nam libero justo laoreet sit amet cursus sit amet dictum.")
                }
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = null,
                    )
                }

                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Default.ThumbDown,
                        contentDescription = null,
                    )
                }

                Spacer(Modifier.weight(1f, true))

                Text("40 replies")

                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Default.Reply,
                        contentDescription = null,
                    )
                }
            }
        }
    }
}