package dev.zt64.hyperion.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.MR
import dev.zt64.hyperion.ui.component.BackButton
import dev.zt64.hyperion.ui.tooling.HyperionPreview
import kotlinx.coroutines.launch

@Composable
fun ErrorScreen(
    exception: Exception
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { BackButton() },
                title = { }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
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
            val context = LocalContext.current
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
                            message = MR.strings.copied_to_clipboard.getString(context)
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

@Preview
@Composable
fun ErrorScreenPreview() {
    HyperionPreview {
        ErrorScreen(Exception("Test exception"))
    }
}