package dev.zt64.hyperion.ui.component

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.zt64.hyperion.api.domain.model.DomainMixPartial
import dev.zt64.hyperion.ui.LocalWindowSizeClass
import dev.zt64.hyperion.ui.component.player.WIDESCREEN_RATIO
import dev.zt64.hyperion.ui.tooling.HyperionPreview

@Composable
fun MixCard(mix: DomainMixPartial, modifier: Modifier = Modifier, onClick: () -> Unit = { }, onLongClick: () -> Unit = { }) {
    ElevatedCard(
        modifier = modifier,
        onClick = onClick,
        onLongClick = onLongClick
    ) {
        val windowSizeClass = LocalWindowSizeClass.current

        if (windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                Thumbnail(
                    modifier = Modifier.width(160.dp),
                    thumbnailUrl = mix.thumbnailUrl
                )

                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = mix.title,
                        style = MaterialTheme.typography.labelMedium,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2
                    )

                    Text(
                        text = mix.subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2
                    )
                }
            }
        } else {
            Column {
                Thumbnail(mix.thumbnailUrl)

                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = mix.title,
                        style = MaterialTheme.typography.labelLarge,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2
                    )

                    Spacer(Modifier.height(2.dp))

                    Text(
                        text = mix.subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2
                    )
                }
            }
        }
    }
}

@Composable
private fun Thumbnail(thumbnailUrl: String, modifier: Modifier = Modifier) {
    Box(
        modifier =
        Modifier
            .height(IntrinsicSize.Min)
            .then(modifier)
    ) {
        ShimmerImage(
            modifier = Modifier.aspectRatio(WIDESCREEN_RATIO),
            url = thumbnailUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.85f))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(34.dp),
                imageVector = Icons.Default.Sensors,
                contentDescription = null
            )
        }
    }
}

@Preview
@Composable
private fun MixCardPreview() {
    HyperionPreview {
        MixCard(
            mix = DomainMixPartial(
                id = "mixId",
                title = "Lorem Ipsum",
                subtitle = "Lorem Ipsum",
                thumbnailUrl = "https://i.ytimg.com/vi/5qap5aO4i9A/hqdefault.jpg"
            )
        )
    }
}