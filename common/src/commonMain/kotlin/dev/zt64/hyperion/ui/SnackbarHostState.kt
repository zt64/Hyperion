package dev.zt64.hyperion.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf

val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState?> { null }

@Composable
internal fun ProvideSnackbarHostState(snackbarHostState: SnackbarHostState, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalSnackbarHostState provides snackbarHostState,
        content = content
    )
}