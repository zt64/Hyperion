package dev.zt64.hyperion.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.NorthWest
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.api.domain.model.DomainVideoPartial
import dev.zt64.hyperion.api.model.SearchResult
import dev.zt64.hyperion.domain.model.search.SearchFilter
import dev.zt64.hyperion.domain.model.search.SearchFilter.Duration
import dev.zt64.hyperion.domain.model.search.SearchFilter.Feature
import dev.zt64.hyperion.domain.model.search.SearchFilter.Sort
import dev.zt64.hyperion.domain.model.search.SearchFilter.Type
import dev.zt64.hyperion.domain.model.search.SearchFilter.UploadDate
import dev.zt64.hyperion.resources.MR
import dev.zt64.hyperion.ui.component.BackButton
import dev.zt64.hyperion.ui.component.LoadingIndicator
import dev.zt64.hyperion.ui.component.VideoCard
import dev.zt64.hyperion.ui.form.Form
import dev.zt64.hyperion.ui.model.SearchScreenModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object SearchScreen : Screen {
    @Composable
    override fun Content() {
        val model: SearchScreenModel = koinScreenModel()
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        val focusManager = LocalFocusManager.current
        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(Unit) {
            if (model.textFieldValue.text.isEmpty()) {
                focusRequester.requestFocus()
            }
        }

        if (model.showFilterSheet) {
            FilterForm(
                onDismissRequest = { model.showFilterSheet = false }
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
                                .onFocusChanged { model.searchActive = it.isFocused },
                            value = model.textFieldValue,
                            onValueChange = model::textFieldValueChange,
                            textStyle = MaterialTheme.typography.bodyLarge,
                            placeholder = { Text(stringResource(MR.strings.search)) },
                            trailingIcon = model
                                .textFieldValue
                                .text
                                .takeIf(String::isNotBlank)
                                ?.let {
                                    {
                                        IconButton(onClick = model::clearSearch) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = stringResource(
                                                    MR.strings.clear
                                                )
                                            )
                                        }
                                    }
                                },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions =
                            KeyboardActions(
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
                                unfocusedIndicatorColor = Color.Transparent
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
                                    }.fillMaxWidth()
                                    .animateItem(),
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
                    val state = rememberLazyListState()

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = state,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(
                            count = results.itemCount,
                            // key = results.itemKey { it.etag + it.id },
                            contentType = results.itemContentType()
                        ) { index ->
                            val result = results[index] ?: return@items

                            when (result) {
                                is SearchResult.Video -> {
                                    VideoCard(
                                        DomainVideoPartial(
                                            id = result.id,
                                            title = result.title,
                                            viewCount = "",
                                            publishedTimeText = "..."
                                        )
                                    )
                                }
                                // is SearchResult.Video -> VideoCard(result)

                                // is SearchResult.Channel -> {
                                //     ChannelCard(
                                //         channel = result,
                                //         onLongClick = {
                                //         },
                                //         onClickSubscribe = {
                                //         }
                                //     )
                                // }
                                //
                                // is SearchResult.Playlist -> PlaylistCard(result)
                                //
                                else -> Text(result.etag)
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterForm(onDismissRequest: () -> Unit) {
    Form(
        onDismissRequest = onDismissRequest,
        title = {
            Text(stringResource(MR.strings.filter))
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 160.dp)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            @Composable
            fun Header(title: StringResource, icon: ImageVector) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null
                    )

                    Text(
                        text = stringResource(title),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            var selectedSort by rememberSaveable {
                mutableStateOf(Sort.RELEVANCE)
            }

            var selectedUploadDate: UploadDate? by rememberSaveable {
                mutableStateOf(null)
            }

            var selectedType: Type? by rememberSaveable {
                mutableStateOf(null)
            }

            var selectedDuration: Duration? by rememberSaveable {
                mutableStateOf(null)
            }

            val selectedFeatures = mutableStateListOf<Feature>()

            fun isCompatible(filter: SearchFilter): Boolean {
                return selectedType == Type.VIDEO ||
                    selectedType == null ||
                    filter !is Duration &&
                    filter !is Feature &&
                    filter !is UploadDate
            }

            Header(
                title = MR.strings.sort_by,
                icon = Icons.AutoMirrored.Default.Sort
            )

            SingleChoiceSegmentedButtonRow {
                Sort.entries.forEachIndexed { index, sort ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = Sort.entries.size
                        ),
                        selected = selectedSort == sort,
                        onClick = { selectedSort = sort },
                        label = {
                            Text(stringResource(sort.displayName))
                        }
                    )
                }
            }

            Header(
                title = MR.strings.upload_date,
                icon = Icons.Default.CalendarMonth
            )

            SingleChoiceSegmentedButtonRow {
                UploadDate.entries.forEachIndexed { index, uploadDate ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = UploadDate.entries.size
                        ),
                        enabled = isCompatible(uploadDate),
                        selected = selectedUploadDate == uploadDate,
                        onClick = {
                            selectedUploadDate =
                                uploadDate.takeUnless { it == selectedUploadDate }
                        },
                        label = {
                            Text(stringResource(uploadDate.displayName))
                        }
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            Header(
                title = MR.strings.type,
                icon = Icons.Default.Star
            )

            SingleChoiceSegmentedButtonRow {
                Type.entries.forEachIndexed { index, type ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = Type.entries.size
                        ),
                        enabled = isCompatible(type),
                        selected = selectedType == type,
                        onClick = {
                            selectedType = type.takeUnless { it == selectedType }
                        },
                        label = {
                            Text(stringResource(type.displayName))
                        }
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            Header(
                title = MR.strings.duration,
                icon = Icons.Default.Star
            )

            SingleChoiceSegmentedButtonRow {
                Duration.entries.forEachIndexed { index, duration ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = Duration.entries.size
                        ),
                        enabled = isCompatible(duration),
                        selected = selectedDuration == duration,
                        onClick = {
                            selectedDuration = duration.takeUnless { it == selectedDuration }
                        },
                        label = {
                            Text(stringResource(duration.displayName))
                        }
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            Header(
                title = MR.strings.features,
                icon = Icons.Default.Star
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start)
            ) {
                Feature.entries.forEach { filter ->
                    val selected by remember {
                        derivedStateOf { filter in selectedFeatures }
                    }

                    FilterChip(
                        selected = selected,
                        enabled = isCompatible(filter),
                        onClick = {
                            if (selected) {
                                selectedFeatures.remove(filter)
                            } else {
                                selectedFeatures += filter
                            }
                        },
                        label = {
                            Text(stringResource(filter.displayName))
                        }
                    )
                }
            }
        }
    }
}