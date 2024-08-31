package dev.zt64.hyperion.ui.component

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.api.domain.model.DomainTagPartial
import dev.zt64.hyperion.resources.MR
import dev.zt64.hyperion.ui.LocalWindowSizeClass
import dev.zt64.hyperion.ui.screen.TagScreen
import dev.zt64.hyperion.ui.tooling.HyperionPreview

@Composable
fun TagCard(tag: DomainTagPartial, modifier: Modifier = Modifier) {
    val navigator = LocalNavigator.currentOrThrow

    TagCard(
        tag = tag,
        onClick = { navigator.push(TagScreen(tag.name)) },
        modifier = modifier
    )
}

@Composable
fun TagCard(
    tag: DomainTagPartial,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val windowSizeClass = LocalWindowSizeClass.current

    ElevatedCard(
        modifier = modifier,
        onClick = onClick
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
                        text = "$it ${stringResource(MR.strings.videos)}",
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                tag.channelsCount?.let {
                    Text(
                        text = "$it ${stringResource(MR.strings.channels)}",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun TagCardPreview() {
    HyperionPreview {
        TagCard(
            tag = DomainTagPartial(
                name = "Music",
                backgroundColor = 0xFF1ed760,
                videosCount = "123",
                channelsCount = "456"
            ),
            onClick = {}
        )
    }
}