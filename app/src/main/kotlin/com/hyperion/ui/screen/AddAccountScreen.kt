package com.hyperion.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hyperion.R
import com.hyperion.ui.viewmodel.AddAccountViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun AddAccountScreen(
    viewModel: AddAccountViewModel = getViewModel(),
    onClickBack: () -> Unit
) {
    when (val state = viewModel.state) {
        AddAccountViewModel.State.Loading -> {
            AddAccountScreenLoading(onClickBack)
        }

        is AddAccountViewModel.State.Loaded -> {
            AddAccountScreenLoaded(
                code = state.code,
                onClickBack = onClickBack,
                onClickActivate = viewModel::activate
            )
        }

        is AddAccountViewModel.State.Error -> {
            ErrorScreen(
                exception = state.error,
                onClickBack = onClickBack
            )
        }
    }
}

@Composable
private fun AddAccountScreenLoading(onClickBack: () -> Unit) {
    Scaffold(
        topBar = {
            LargeTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                title = {
                    Text(stringResource(R.string.add_account))
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun AddAccountScreenLoaded(
    code: String,
    onClickBack: () -> Unit,
    onClickActivate: () -> Unit
) {
    Scaffold(
        topBar = {
            LargeTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                title = {
                    Text(stringResource(R.string.add_account))
                }
            )
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
                SelectionContainer {
                    Text(
                        text = code,
                        style = MaterialTheme.typography.displayLarge
                    )
                }

                OutlinedButton(
                    onClick = onClickActivate,
                    contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInBrowser,
                        contentDescription = null
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Open in browser")
                }
            }
        }
    }
}