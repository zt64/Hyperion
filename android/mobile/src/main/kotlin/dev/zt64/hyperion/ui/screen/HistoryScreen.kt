package dev.zt64.hyperion.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.MR
import dev.zt64.hyperion.ui.component.BackButton
import dev.zt64.hyperion.ui.component.VideoCard
import dev.zt64.hyperion.ui.viewmodel.HistoryViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HistoryScreen() {
    val viewModel: HistoryViewModel = koinViewModel()

    Scaffold(
        topBar = {
            MediumTopAppBar(
                navigationIcon = { BackButton() },
                title = { Text(stringResource(MR.strings.history)) }
            )
        }
    ) { paddingValues ->
        val history = viewModel.history.collectAsLazyPagingItems()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(
                count = history.itemCount,
                key = history.itemKey { it.id },
                contentType = history.itemContentType()
            ) { index ->
                val video = history[index] ?: return@items

                VideoCard(video)
            }
        }
    }
}