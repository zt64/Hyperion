package com.hyperion.ui.component

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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hyperion.domain.manager.PreferencesManager
import com.hyperion.domain.model.DomainSearch
import org.koin.androidx.compose.get

@Composable
fun MixCard(
    modifier: Modifier = Modifier,
    mix: DomainSearch.Result.Mix,
    onClick: () -> Unit,
    onLongClick: () -> Unit = { },
    prefs: PreferencesManager = get()
) {
    ElevatedCard(
        modifier = modifier
            .clip(CardDefaults.elevatedShape)
            .combinedClickable(
                onClick = onClick,
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
                    modifier = Modifier
                        .heightIn(min = 70.dp)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = mix.title,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 2
                    )

                    Text(
                        text = mix.subtitle,
                        style = MaterialTheme.typography.bodySmall,
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
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = mix.title,
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 2
                    )

                    Text(
                        modifier = Modifier.padding(top = 4.dp),
                        text = mix.subtitle,
                        style = MaterialTheme.typography.labelSmall
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
        AsyncImage(
            modifier = Modifier.aspectRatio(16f / 9f),
            model = thumbnailUrl,
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