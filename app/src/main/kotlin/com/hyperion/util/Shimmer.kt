package com.hyperion.util

import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer

fun Modifier.shimmer(): Modifier = composed {
    val localElevation = LocalAbsoluteTonalElevation.current

    placeholder(
        visible = true,
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(localElevation + 2.dp),
        highlight = PlaceholderHighlight.shimmer(
            highlightColor = MaterialTheme.colorScheme.surfaceColorAtElevation(localElevation + 3.dp)
        )
    )
}