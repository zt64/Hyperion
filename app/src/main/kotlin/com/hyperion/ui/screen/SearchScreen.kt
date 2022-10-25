package com.hyperion.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.hyperion.R
import com.hyperion.domain.model.DomainSearch
import com.hyperion.domain.model.DomainVideoPartial
import com.hyperion.ui.component.ChannelCard
import com.hyperion.ui.component.MixCard
import com.hyperion.ui.component.PlaylistCard
import com.hyperion.ui.component.VideoCard
import com.hyperion.ui.navigation.AppDestination
import com.hyperion.ui.navigation.BackstackNavigator
import com.hyperion.ui.viewmodel.SearchViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = getViewModel(),
    navigator: BackstackNavigator<AppDestination>,
    onClickBack: () -> Unit,
    onClickChannel: (id: String) -> Unit,
    onClickPlaylist: (id: String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var showResults by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    SearchField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(viewModel.focusRequester)
                            .onFocusChanged { showResults = !it.isFocused },
                        value = viewModel.search,
                        onValueChange = viewModel::getSuggestions,
                        placeholder = { Text(stringResource(R.string.search)) },
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
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
                                    channel = result.channel,
                                    timestamp = result.timestamp
                                ),
                                onClick = {
                                    navigator.push(AppDestination.Player(result.id))
                                },
                                onClickChannel = {
                                    onClickChannel(result.channel!!.id)
                                }
                            )
                        }

                        is DomainSearch.Result.Channel -> {
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

                        is DomainSearch.Result.Playlist -> {
                            PlaylistCard(
                                playlist = result,
                                onClick = {
                                    onClickPlaylist(result.id)
                                }
                            )
                        }

                        is DomainSearch.Result.Mix -> {
                            MixCard(
                                mix = result,
                                onClick = {

                                }
                            )
                        }
                    }
                }

                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        results.loadState.apply {
                            when (results.loadState.append) {
                                is LoadState.NotLoading -> Unit
                                is LoadState.Loading -> {
                                    CircularProgressIndicator()
                                }

                                is LoadState.Error -> {
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
                }
            } else {
                items(viewModel.suggestions) { suggestion ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.search(suggestion)
                                focusManager.clearFocus()
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
                                viewModel.getSuggestions(suggestion)
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

@Composable
private fun SearchField(
    modifier: Modifier,
    value: String,
    onValueChange: ((String) -> Unit),
    placeholder: @Composable () -> Unit,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions
) {
    BasicTextField(
        modifier = modifier
            .fillMaxWidth()
            .clip(CircleShape),
        value = value,
        onValueChange = onValueChange,
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onSurface
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        singleLine = true,
        decorationBox = { innerTextField ->
            TextFieldDefaults.TextFieldDecorationBox(
                value = value,
                innerTextField = innerTextField,
                placeholder = placeholder,
                trailingIcon = {
                    if (value.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                onValueChange("")
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(R.string.clear)
                            )
                        }
                    }
                },
                singleLine = true,
                enabled = true,
                interactionSource = remember { MutableInteractionSource() },
                visualTransformation = VisualTransformation.None,
                contentPadding = PaddingValues(
                    start = 14.dp,
                    end = 12.dp,
                    top = 10.dp,
                    bottom = 10.dp
                ),
                colors = TextFieldDefaults.textFieldColors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp)
                )
            )
        },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions
    )
}