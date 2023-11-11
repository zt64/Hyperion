package dev.zt64.hyperion.ui.model

import androidx.compose.runtime.*
import androidx.paging.*
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.zt64.hyperion.domain.manager.ShareManager
import dev.zt64.hyperion.domain.paging.BrowsePagingSource
import dev.zt64.innertube.domain.model.DomainPlaylist
import dev.zt64.innertube.domain.model.DomainVideoPartial
import dev.zt64.innertube.domain.repository.InnerTubeRepository
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

class PlaylistScreenModel(
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
    var playlist by mutableStateOf<DomainPlaylist?>(null)
        private set
    var videos by mutableStateOf(emptyFlow<PagingData<DomainVideoPartial>>())
        private set
    // val snackbarHostState = SnackbarHostState()

    init {
        state = State.Loading

        screenModelScope.launch {
            try {
                playlist = innerTube.getPlaylist(playlistId)
                videos = Pager(pagingConfig) {
                    BrowsePagingSource { key ->
                        key?.let { innerTube.getPlaylist(playlistId, key) } ?: playlist!!
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
        shareManager.share(playlist!!.shareUrl, playlist!!.name)
    }

    fun play() {

    }

    fun shuffle() {

    }

    fun download() {

    }
}