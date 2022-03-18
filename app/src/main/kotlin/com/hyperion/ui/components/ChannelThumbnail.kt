package com.hyperion.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.RenderVectorGroup
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.hyperion.util.InvidiousApi

@Composable
fun ChannelThumbnail(
    modifier: Modifier = Modifier,
    authorId: String,
) {
    var avatarUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        avatarUrl = InvidiousApi.getChannel(authorId).authorThumbnails.last().url
    }

    AsyncImage(
        modifier = Modifier
            .clip(CircleShape)
            .size(36.dp)
            .then(modifier),
        model = ImageRequest.Builder(LocalContext.current)
            .data(avatarUrl)
            .crossfade(true)
            .build(),
        placeholder = Icons.Default.AccountCircle.let { icon ->
            rememberVectorPainter(
                defaultWidth = icon.defaultWidth,
                defaultHeight = icon.defaultHeight,
                viewportWidth = icon.viewportWidth,
                viewportHeight = icon.viewportHeight,
                name = icon.name,
                tintColor = MaterialTheme.colorScheme.onSurface,
                tintBlendMode = icon.tintBlendMode,
                content = { _, _ -> RenderVectorGroup(icon.root) }
            )
        },
        contentDescription = null
    )
}