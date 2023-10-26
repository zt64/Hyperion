package dev.zt64.hyperion.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.NorthWest
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.MR
import dev.zt64.hyperion.ui.component.*
import dev.zt64.hyperion.ui.model.SearchScreenModel
import dev.zt64.innertube.domain.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object SearchScreen : Screen {
    @Composable
    override fun Content() {
        val model: SearchScreenModel = getScreenModel()
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        val focusManager = LocalFocusManager.current
        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(Unit) {
            if (model.textFieldValue.text.isEmpty()) {
                focusRequester.requestFocus()
            }
        }

        // if (viewModel.showFilterSheet) {
        //     FilterSheet(
        //         onDismissRequest = { viewModel.showFilterSheet = false }
        //     )
        // }

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
                                .onFocusChanged { model.searchActive = it.isFocused },
                            value = model.textFieldValue,
                            onValueChange = model::textFieldValueChange,
                            textStyle = MaterialTheme.typography.bodyLarge,
                            placeholder = { Text(stringResource(MR.strings.search)) },
                            trailingIcon = model.textFieldValue.text.takeIf(String::isNotBlank)
                                ?.let {
                                    {
                                        IconButton(onClick = model::clearSearch) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = stringResource(MR.strings.clear)
                                            )
                                        }
                                    }
                                },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    if (model.textFieldValue.text.isNotBlank()) {
                                        model.search()
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
                            onClick = { model.showFilterSheet = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = stringResource(MR.strings.filter)
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
                targetState = model.searchActive,
                label = "Search"
            ) { searchActive ->
                if (searchActive) {
                    val coroutineScope = rememberCoroutineScope()

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(
                            items = model.suggestions,
                            key = { it }
                        ) { suggestion ->
                            Row(
                                modifier = Modifier
                                    .clickable {
                                        coroutineScope.launch {
                                            model.selectSuggestion(suggestion)
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
                                        model.replaceSuggestion(suggestion)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NorthWest,
                                        contentDescription = stringResource(MR.strings.replace)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    val results = model.results.collectAsLazyPagingItems()

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
                            LoadingIndicator(results.loadState)
                        }
                    }
                }
            }
        }
    }
}

// @OptIn(ExperimentalLayoutApi::class)
// @Composable
// private fun FilterSheet(onDismissRequest: () -> Unit) {
//     ModalBottomSheet(onDismissRequest) {
//         Column(
//             modifier = Modifier
//                 .fillMaxWidth()
//                 .heightIn(min = 160.dp)
//                 .padding(16.dp),
//             horizontalAlignment = Alignment.Start,
//             verticalArrangement = Arrangement.spacedBy(28.dp)
//         ) {
//             @Composable
//             fun <T> FilterFlowRow(
//                 title: StringResource,
//                 icon: ImageVector,
//                 items: EnumEntries<T>
//             ) where T : Enum<T>, T : SearchFilter {
//                 Column {
//                     Row(
//                         horizontalArrangement = Arrangement.spacedBy(6.dp),
//                         verticalAlignment = Alignment.CenterVertically
//                     ) {
//                         Icon(
//                             imageVector = icon,
//                             contentDescription = null,
//                         )
//
//                         Text(
//                             text = stringResource(title),
//                             style = MaterialTheme.typography.titleLarge
//                         )
//                     }
//
//                     FlowRow(
//                         horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start),
//                     ) {
//                         items.forEach { filter ->
//                             var selected by rememberSaveable {
//                                 mutableStateOf(false)
//                             }
//
//                             FilterChip(
//                                 selected = selected,
//                                 onClick = { selected = !selected },
//                                 label = {
//                                     Text(stringResource(filter.displayName))
//                                 },
//                                 enabled = true
//                             )
//                         }
//                     }
//                 }
//             }
//
//             FilterFlowRow(
//                 title = MR.strings.upload_date,
//                 icon = Icons.Default.CalendarToday,
//                 items = SearchFilter.UploadDate.entries
//             )
//
//             FilterFlowRow(
//                 title = MR.strings.type,
//                 icon = Icons.Default.FilterList,
//                 items = SearchFilter.Type.entries
//             )
//
//             FilterFlowRow(
//                 title = MR.strings.duration,
//                 icon = Icons.Default.Timer,
//                 items = SearchFilter.Duration.entries
//             )
//
//             FilterFlowRow(
//                 title = MR.strings.features,
//                 icon = Icons.Default.Star,
//                 items = SearchFilter.Feature.entries
//             )
//
//             FilterFlowRow(
//                 title = MR.strings.sort_by,
//                 icon = Icons.AutoMirrored.Default.Sort,
//                 items = SearchFilter.Sort.entries
//             )
//         }
//     }
// }