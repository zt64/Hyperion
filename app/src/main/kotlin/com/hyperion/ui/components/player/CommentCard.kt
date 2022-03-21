package com.hyperion.ui.components.player

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentCard(
    onLike: () -> Unit,
    onDislike: () -> Unit,
    onReply: () -> Unit
) {
    ElevatedCard {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(36.dp),
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null
                )

                Column {
                    Text("Comment Author")
                    Text(
                        text = "Date posted",
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Spacer(Modifier.weight(1f, true))

                IconButton(
                    onClick = { /*TODO*/ }
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null
                    )
                }
            }

            Text(
                text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam sem metus, accumsan eu euismod nec, malesuada id lectus.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}