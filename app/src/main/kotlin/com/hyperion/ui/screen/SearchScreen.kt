package com.hyperion.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.NorthWest
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.hyperion.R
import com.hyperion.ui.component.*
import com.hyperion.ui.navigation.AppDestination
import com.hyperion.ui.viewmodel.SearchViewModel
import com.hyperion.util.rememberLazyListState
import com.zt.innertube.domain.model.*
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = getViewModel(),
    navController: NavController<AppDestination>,
    onClickBack: () -> Unit,
    onClickChannel: (id: String) -> Unit,
    onClickPlaylist: (id: String) -> Unit,
    onClickTag: (name: String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var showResults by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (viewModel.textFieldValue.text.isEmpty()) {
            viewModel.focusRequester.requestFocus()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 54.dp)
                            .focusRequester(viewModel.focusRequester)
                            .onFocusChanged { showResults = !it.isFocused },
                        value = viewModel.textFieldValue,
                        onValueChange = viewModel::textFieldValueChange,
                        textStyle = MaterialTheme.typography.bodyLarge,
                        placeholder = { Text(stringResource(R.string.search)) },
                        trailingIcon = viewModel.textFieldValue.text.takeIf(String::isNotBlank)?.let {
                            {
                                IconButton(onClick = viewModel::clearSearch) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = stringResource(R.string.clear)
                                    )
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                if (viewModel.textFieldValue.text.isNotBlank()) {
                                    viewModel.search()
                                    focusManager.clearFocus()
                                }
                            }
                        ),
                        singleLine = true,
                        shape = CircleShape,
                        colors = TextFieldDefaults.textFieldColors(
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { }
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = stringResource(R.string.filter)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        val results = viewModel.results.collectAsLazyPagingItems()
        val state = results.rememberLazyListState()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 14.dp),
            state = state,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (showResults) {
                items(
                    items = results,
                    key = { it.hashCode() }
                ) { result ->
                    if (result == null) return@items

                    when (result) {
                        is DomainVideoPartial -> {
                            VideoCard(
                                video = result,
                                onClick = {
                                    navController.navigate(AppDestination.Player(result.id))
                                },
                                onClickChannel = {
                                    onClickChannel(result.channel!!.id)
                                }
                            )
                        }

                        is DomainChannelPartial -> {
                            ChannelCard(
                                channel = result,
                                onClick = {
                                    onClickChannel(result.id)
                                },
                                onLongClick = {

                                },
                                onClickSubscribe = {

                                }
                            )
                        }

                        is DomainPlaylistPartial -> {
                            PlaylistCard(
                                playlist = result,
                                onClick = {
                                    onClickPlaylist(result.id)
                                }
                            )
                        }

                        is DomainMixPartial -> {
                            MixCard(
                                mix = result,
                                onClick = {

                                }
                            )
                        }

                        is DomainTagPartial -> {
                            TagCard(
                                tag = result,
                                onClick = {
                                    onClickTag(result.name)
                                }
                            )
                        }
                    }
                }

                item {
                    results.loadState.apply {
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
            } else {
                items(viewModel.suggestions) { suggestion ->
                    val coroutineScope = rememberCoroutineScope()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                coroutineScope.launch {
                                    viewModel.selectSuggestion(suggestion)
                                    delay(10)
                                    focusManager.clearFocus()
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = suggestion,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(Modifier.weight(1f, true))

                        IconButton(
                            onClick = {
                                viewModel.replaceSuggestion(suggestion)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.NorthWest,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }
}