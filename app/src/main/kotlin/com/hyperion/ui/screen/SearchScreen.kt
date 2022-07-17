package com.hyperion.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.hyperion.R
import com.hyperion.domain.model.DomainSearch
import com.hyperion.domain.model.DomainVideoPartial
import com.hyperion.ui.component.ChannelCard
import com.hyperion.ui.component.PlaylistCard
import com.hyperion.ui.component.VideoCard
import com.hyperion.ui.screen.destinations.ChannelScreenDestination
import com.hyperion.ui.screen.destinations.PlayerScreenDestination
import com.hyperion.ui.viewmodel.SearchViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalFoundationApi::class)
@Destination
@Composable
fun SearchScreen(
    navigator: DestinationsNavigator,
    viewModel: SearchViewModel = getViewModel()
) {
    val focusRequester = remember { FocusRequester() }
    val results = viewModel.results.collectAsLazyPagingItems()
    val swipeRefreshState = rememberSwipeRefreshState(results.loadState.refresh == LoadState.Loading)

    LaunchedEffect(Unit) {
        focusRequester.captureFocus()
    }

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = results::refresh,
        indicator = { state, trigger ->
            SwipeRefreshIndicator(
                state = state,
                refreshTriggerDistance = trigger,
                scale = true,
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    ) {
        val focusManager = LocalFocusManager.current
        var showResults by remember { mutableStateOf(false) }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            stickyHeader {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .focusRequester(focusRequester)
                            .onFocusChanged { showResults = !it.isFocused },
                        value = viewModel.search,
                        onValueChange = { viewModel.getSuggestions(it) },
                        singleLine = true,
                        label = { Text(stringResource(R.string.search)) },
                        trailingIcon = {
                            Icon(
                                modifier = Modifier.clickable {
                                    if (viewModel.search.isNotBlank()) {
                                        viewModel.getResults()
                                        focusManager.clearFocus()
                                    }
                                },
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(R.string.search)
                            )
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                if (viewModel.search.isNotBlank()) {
                                    viewModel.getResults()
                                    focusManager.clearFocus()
                                }
                            }
                        )
                    )
                }
            }

            if (showResults) {
                items(results) { result ->
                    if (result == null) return@items

                    when (result) {
                        is DomainSearch.Result.Video -> {
                            VideoCard(
                                video = DomainVideoPartial(
                                    id = result.id,
                                    title = result.title,
                                    subtitle = result.subtitle,
                                    thumbnailUrl = result.thumbnailUrl,
                                    author = result.author,
                                    timestamp = result.timestamp
                                ),
                                onClick = { navigator.navigate(PlayerScreenDestination(result.id)) },
                                onChannelClick = { navigator.navigate(ChannelScreenDestination(result.author!!.id)) }
                            )
                        }
                        is DomainSearch.Result.Channel -> {
                            ChannelCard(
                                channel = result,
                                onClick = { navigator.navigate(ChannelScreenDestination(result.id)) },
                                onSubscribe = { }
                            )
                        }
                        is DomainSearch.Result.Playlist -> {
                            PlaylistCard(
                                playlist = result,
                                onClick = { }
                            )
                        }
                    }
                }

                item {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        results.loadState.apply {
                            when (append) {
                                is LoadState.Loading -> {
                                    CircularProgressIndicator(
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                                is LoadState.Error -> {
                                    (append as LoadState.Error).error.message?.let {
                                        Text(
                                            text = it,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                                is LoadState.NotLoading -> Divider()
                            }
                        }
                    }
                }
            } else {
                items(viewModel.suggestions) { suggestion ->
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .fillMaxWidth()
                            .clickable {
                                viewModel.search(suggestion)
                                focusManager.clearFocus()
                            },
                        text = suggestion,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}