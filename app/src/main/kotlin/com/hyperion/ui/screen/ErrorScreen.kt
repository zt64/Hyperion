package com.hyperion.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.hyperion.R
import kotlinx.coroutines.launch

@Composable
fun ErrorScreen(
    exception: Exception,
    onClickBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                title = { }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
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
                    contentDescription = stringResource(R.string.error)
                )

                Text(
                    text = stringResource(R.string.error_occurred),
                    style = MaterialTheme.typography.titleLarge
                )

                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(exception.stackTraceToString()))
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = context.getString(R.string.copied_to_clipboard)
                            )
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.CopyAll,
                        contentDescription = stringResource(R.string.error)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(stringResource(R.string.copy_stacktrace))
                }
            }
        }
    }
}