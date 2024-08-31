package dev.zt64.hyperion.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.resources.MR
import dev.zt64.hyperion.ui.component.AdaptiveTopBar
import dev.zt64.hyperion.ui.model.AddAccountScreenModel
import dev.zt64.hyperion.ui.model.AddAccountScreenModel.State

object AddAccountScreen : Screen {
    @Composable
    override fun Content() {
        val model: AddAccountScreenModel = koinScreenModel()

        when (val state = model.state) {
            is State.Loading -> Loading()
            is State.Pending -> Loaded(
                code = state.code,
                onClickActivate = model::activate
            )
            is State.Ready -> Unit
            is State.Error -> ErrorScreenContent(state.error)
        }
    }

    @Composable
    private fun Loading() {
        Scaffold(
            topBar = {
                AdaptiveTopBar(
                    title = { Text(stringResource(MR.strings.add_account)) }
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
    private fun Loaded(code: String, onClickActivate: () -> Unit) {
        Scaffold(
            topBar = {
                AdaptiveTopBar(
                    title = { Text(stringResource(MR.strings.add_account)) }
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
}