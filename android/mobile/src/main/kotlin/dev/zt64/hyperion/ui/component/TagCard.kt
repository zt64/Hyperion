package dev.zt64.hyperion.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import dev.olshevski.navigation.reimagined.navigate
import dev.zt64.hyperion.MR
import dev.zt64.hyperion.ui.LocalWindowSizeClass
import dev.zt64.hyperion.ui.navigation.AppDestination
import dev.zt64.hyperion.ui.navigation.LocalNavController
import dev.zt64.hyperion.ui.tooling.HyperionPreview
import dev.zt64.innertube.domain.model.DomainTagPartial

@Composable
fun TagCard(
    tag: DomainTagPartial,
    modifier: Modifier = Modifier
) {
    val navController = LocalNavController.current
    val windowSizeClass = LocalWindowSizeClass.current

    ElevatedCard(
        modifier = modifier,
        onClick = {
            navController.navigate(AppDestination.Tag(tag.name))
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(Color(tag.backgroundColor)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = Icons.Default.Tag,
                    contentDescription = stringResource(MR.strings.tag)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = tag.name,
                    style = MaterialTheme.typography.titleMedium
                )

                tag.videosCount?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                tag.channelsCount?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@PreviewScreenSizes
@Composable
private fun TagCardPreview() {
    HyperionPreview {
        TagCard(
            tag = DomainTagPartial(
                name = "Music",
                backgroundColor = Color.Red.value.toLong(),
                videosCount = "1.2k",
                channelsCount = "1.2k"
            )
        )
    }
}