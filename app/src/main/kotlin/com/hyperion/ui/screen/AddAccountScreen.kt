package com.hyperion.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hyperion.R
import com.hyperion.ui.component.BackButton
import com.hyperion.ui.viewmodel.AddAccountViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddAccountScreen() {
    val viewModel: AddAccountViewModel = koinViewModel()

    when (val state = viewModel.state) {
        AddAccountViewModel.State.Loading -> {
            AddAccountScreenLoading()
        }

        is AddAccountViewModel.State.Loaded -> {
            AddAccountScreenLoaded(
                code = state.code,
                onClickActivate = viewModel::activate
            )
        }

        is AddAccountViewModel.State.Error -> {
            ErrorScreen(
                exception = state.error
            )
        }
    }
}

@Composable
private fun AddAccountScreenLoading() {
    Scaffold(
        topBar = {
            LargeTopAppBar(
                navigationIcon = { BackButton() },
                title = { Text(stringResource(R.string.add_account)) }
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
    onClickActivate: () -> Unit
) {
    Scaffold(
        topBar = {
            LargeTopAppBar(
                navigationIcon = { BackButton() },
                title = { Text(stringResource(R.string.add_account)) }
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