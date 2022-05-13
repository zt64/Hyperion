package com.hyperion.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Timestamp(
    modifier: Modifier = Modifier,
    text: String
) {
    Surface(
        modifier = Modifier
            .padding(4.dp)
            .then(modifier),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f)
    ) {
        Text(
            modifier = Modifier.padding(4.dp),
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onTertiaryContainer
        )
    }
}