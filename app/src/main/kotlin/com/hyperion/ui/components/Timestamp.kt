package com.hyperion.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hyperion.domain.manager.PreferencesManager
import org.koin.androidx.compose.get

@Composable
fun Timestamp(
    modifier: Modifier = Modifier,
    text: String,
    prefs: PreferencesManager = get()
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
            fontSize = 14.sp * prefs.timestampScale
        )
    }
}