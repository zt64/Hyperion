package dev.zt64.hyperion.ui.model

import androidx.compose.runtime.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.zt64.hyperion.domain.manager.ShareManager
import dev.zt64.innertube.domain.repository.InnerTubeRepository
import dev.zt64.innertube.model.Playlist
import dev.zt64.innertube.model.PlaylistItem
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

internal class PlaylistScreenModel(
    private val innerTube: InnerTubeRepository,
    private val pagingConfig: PagingConfig,
    private val shareManager: ShareManager,
    playlistId: String
) : ScreenModel {
    @Immutable
    sealed interface State {
        data object Loaded : State

        data object Loading : State

        data class Error(val exception: Exception) : State
    }

    var state by mutableStateOf<State>(State.Loading)
        private set
    var playlist by mutableStateOf<Playlist?>(null)
        private set
    var videos by mutableStateOf(emptyFlow<PagingData<PlaylistItem>>())
        private set
    // val snackbarHostState = SnackbarHostState()

    init {
        state = State.Loading

        screenModelScope.launch {
            try {
                playlist = innerTube.getPlaylist(playlistId)
                videos =
                    Pager(pagingConfig) {
                        object : PagingSource<String, PlaylistItem>() {
                            override suspend fun load(params: LoadParams<String>) = try {
                                val response =
                                    innerTube
                                        .getPlaylistItems(playlistId, params.key)

                                LoadResult.Page(
                                    data = response.results,
                                    prevKey = null,
                                    nextKey = response.nextToken
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()

                                LoadResult.Error(e)
                            }

                            override fun getRefreshKey(
                                state: PagingState<String, PlaylistItem>
                            ): String? = null
                        }
                    }.flow.cachedIn(screenModelScope)

                state = State.Loaded
            } catch (e: Exception) {
                e.printStackTrace()
                state = State.Error(e)
            }
        }
    }

    fun saveToLibrary() {
        screenModelScope.launch {
            // snackbarHostState.showSnackbar(application.getString(MR.strings.saved_to_library))
        }
    }

    fun sharePlaylist() {
        shareManager.share(playlist!!.shareUrl, playlist!!.title)
    }

    fun play() {
    }

    fun shuffle() {
    }

    fun download() {
    }
}