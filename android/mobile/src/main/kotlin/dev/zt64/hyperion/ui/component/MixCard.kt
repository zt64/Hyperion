package dev.zt64.hyperion.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.zt64.hyperion.LocalNavController
import dev.zt64.hyperion.domain.manager.PreferencesManager
import dev.zt64.hyperion.ui.component.player.WIDESCREEN_RATIO
import dev.zt64.innertube.domain.model.DomainMixPartial
import org.koin.compose.koinInject

@Composable
fun MixCard(
    mix: DomainMixPartial,
    modifier: Modifier = Modifier,
    onLongClick: () -> Unit = { },
) {
    val navController = LocalNavController.current
    val prefs: PreferencesManager = koinInject()

    ElevatedCard(
        modifier = modifier
            .clip(CardDefaults.elevatedShape)
            .combinedClickable(
                onClick = {

                },
                onLongClick = onLongClick
            )
    ) {
        val orientation = LocalConfiguration.current.orientation

        if (orientation == Configuration.ORIENTATION_LANDSCAPE || prefs.compactCard) {
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
                Thumbnail(
                    thumbnailUrl = mix.thumbnailUrl
                )

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
private fun Thumbnail(
    modifier: Modifier = Modifier,
    thumbnailUrl: String
) {
    Box(
        modifier = Modifier
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