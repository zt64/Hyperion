package com.hyperion.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Timestamp(
    modifier: Modifier = Modifier,
    text: String,
    scale: Float
) {
    Surface(
        modifier = Modifier
            .padding(8.dp)
            .then(modifier),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.8f)
    ) {
        Text(
            modifier = Modifier.padding(4.dp),
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            fontSize = 14.sp * scale
        )
    }
}