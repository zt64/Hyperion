package dev.zt64.hyperion.ui.component

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState

/**
 * Loading indicator to display at the end of a list with pagination
 *
 * @param loadState Combined load state of the list
 */
@Composable
fun LoadingIndicator(loadState: CombinedLoadStates) {
    loadState.source.apply {
        when {
            refresh is LoadState.Loading || append is LoadState.Loading -> {
                CircularProgressIndicator()
            }
            append is LoadState.Error -> {
                (append as LoadState.Error).error.message?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}