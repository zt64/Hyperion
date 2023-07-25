package com.hyperion.ui.screen

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.hyperion.R
import com.hyperion.domain.model.search.SearchFilter
import com.hyperion.ui.component.*
import com.hyperion.ui.viewmodel.SearchViewModel
import com.zt.innertube.domain.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.enums.EnumEntries

@Composable
fun SearchScreen() {
    val viewModel: SearchViewModel = koinViewModel()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        if (viewModel.textFieldValue.text.isEmpty()) {
            focusRequester.requestFocus()
        }
    }

    if (viewModel.showFilterSheet) {
        FilterSheet(
            onDismissRequest = { viewModel.showFilterSheet = false }
        )
    }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 54.dp)
                            .focusRequester(focusRequester)
                            .onFocusChanged { viewModel.searchActive = it.isFocused },
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
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        )
                    )
                },
                navigationIcon = { BackButton() },
                actions = {
                    IconButton(
                        onClick = { viewModel.showFilterSheet = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = stringResource(R.string.filter)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { paddingValues ->
        AnimatedContent(
            modifier = Modifier.padding(paddingValues),
            targetState = viewModel.searchActive,
            label = "Search"
        ) { searchActive ->
            if (searchActive) {
                val coroutineScope = rememberCoroutineScope()

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = viewModel.suggestions,
                        key = { it }
                    ) { suggestion ->
                        Row(
                            modifier = Modifier
                                .clickable {
                                    coroutineScope.launch {
                                        viewModel.selectSuggestion(suggestion)
                                        delay(50)
                                        focusManager.clearFocus()
                                    }
                                }
                                .fillMaxWidth()
                                .animateItemPlacement(),
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
                                    contentDescription = stringResource(R.string.replace)
                                )
                            }
                        }
                    }
                }
            } else {
                val results = viewModel.results.collectAsLazyPagingItems()

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        count = results.itemCount,
                        key = results.itemKey { it.hashCode() },
                        contentType = results.itemContentType()
                    ) { index ->
                        val result = results[index] ?: return@items

                        when (result) {
                            is DomainVideoPartial -> VideoCard(result)

                            is DomainChannelPartial -> {
                                ChannelCard(
                                    channel = result,
                                    onLongClick = {

                                    },
                                    onClickSubscribe = {

                                    }
                                )
                            }

                            is DomainPlaylistPartial -> PlaylistCard(result)
                            is DomainMixPartial -> MixCard(result)
                            is DomainTagPartial -> TagCard(result)
                        }
                    }

                    item {
                        results.loadState.source.apply {
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
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterSheet(onDismissRequest: () -> Unit) {
    ModalBottomSheet(onDismissRequest) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 160.dp)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            @Composable
            fun <T> FilterFlowRow(
                @StringRes title: Int,
                icon: ImageVector,
                items: EnumEntries<T>
            ) where T : Enum<T>, T : SearchFilter {
                Column {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                        )

                        Text(
                            text = stringResource(title),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start),
                    ) {
                        items.forEach { filter ->
                            var selected by rememberSaveable {
                                mutableStateOf(false)
                            }

                            FilterChip(
                                selected = selected,
                                onClick = { selected = !selected },
                                label = {
                                    Text(stringResource(filter.displayName))
                                },
                                enabled = true
                            )
                        }
                    }
                }
            }

            FilterFlowRow(
                title = R.string.upload_date,
                icon = Icons.Default.CalendarToday,
                items = SearchFilter.UploadDate.entries
            )

            FilterFlowRow(
                title = R.string.type,
                icon = Icons.Default.FilterList,
                items = SearchFilter.Type.entries
            )

            FilterFlowRow(
                title = R.string.duration,
                icon = Icons.Default.Timer,
                items = SearchFilter.Duration.entries
            )

            FilterFlowRow(
                title = R.string.features,
                icon = Icons.Default.Star,
                items = SearchFilter.Feature.entries
            )

            FilterFlowRow(
                title = R.string.sort_by,
                icon = Icons.Default.Sort,
                items = SearchFilter.Sort.entries
            )
        }
    }
}