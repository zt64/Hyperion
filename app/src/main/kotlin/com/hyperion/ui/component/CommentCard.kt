package com.hyperion.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zt.innertube.domain.model.DomainComment

@Composable
fun CommentCard(
    comment: DomainComment,
    onClickLike: () -> Unit,
    onClickDislike: () -> Unit,
    onClickReply: () -> Unit
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
            }

            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}