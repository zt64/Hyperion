package dev.zt64.hyperion.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.zt64.hyperion.ui.LocalWindowSizeClass
import dev.zt64.hyperion.ui.navigation.LocalNavController
import dev.zt64.innertube.domain.model.DomainComment

@Composable
fun CommentCard(
    comment: DomainComment,
    onClickLike: () -> Unit,
    onClickDislike: () -> Unit,
    onClickReply: () -> Unit
) {
    val navController = LocalNavController.current
    val windowSizeClass = LocalWindowSizeClass.current

    ElevatedCard {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .size(36.dp)
                        .clickable {

                        },
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