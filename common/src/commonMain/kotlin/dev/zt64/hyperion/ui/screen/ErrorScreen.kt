package dev.zt64.hyperion.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import dev.icerock.moko.resources.desc.desc
import dev.zt64.hyperion.resources.MR
import dev.zt64.hyperion.ui.component.AdaptiveTopBar
import kotlinx.coroutines.launch

@Composable
fun ErrorScreenContent(
    exception: Exception,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = { AdaptiveTopBar() },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .wrapContentSize(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val coroutineScope = rememberCoroutineScope()
            val clipboardManager = LocalClipboardManager.current

            Icon(
                modifier = Modifier.size(48.dp),
                imageVector = Icons.Default.Error,
                tint = MaterialTheme.colorScheme.error,
                contentDescription = stringResource(MR.strings.error)
            )

            Text(
                text = stringResource(MR.strings.error_occurred),
                style = MaterialTheme.typography.titleLarge
            )

            Button(
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                onClick = {
                    clipboardManager.setText(AnnotatedString(exception.stackTraceToString()))
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = MR.strings.copied_to_clipboard.desc().toString()
                        )
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.CopyAll,
                    contentDescription = stringResource(MR.strings.error)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(stringResource(MR.strings.copy_stacktrace))
            }
        }
    }
}