package com.hyperion.ui.viewmodel

import android.app.Application
import android.content.Intent
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.hyperion.R
import com.hyperion.domain.paging.BrowsePagingSource
import com.zt.innertube.domain.model.DomainPlaylist
import com.zt.innertube.domain.model.DomainVideoPartial
import com.zt.innertube.domain.repository.InnerTubeRepository
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val application: Application,
    private val innerTube: InnerTubeRepository,
    private val pagingConfig: PagingConfig,
    playlistId: String
) : ViewModel() {
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
    val snackbarHostState = SnackbarHostState()

    init {
        state = State.Loading

        viewModelScope.launch {
            try {
                playlist = innerTube.getPlaylist(playlistId)
                videos = Pager(pagingConfig) {
                    BrowsePagingSource { key ->
                        if (key == null) playlist!! else innerTube.getPlaylist(playlistId, key)
                    }
                }.flow.cachedIn(viewModelScope)

                state = State.Loaded
            } catch (e: Exception) {
                e.printStackTrace()
                state = State.Error(e)
            }
        }
    }

    fun saveToLibrary() {
        viewModelScope.launch {
            snackbarHostState.showSnackbar(application.getString(R.string.saved_to_library))
        }
    }

    fun sharePlaylist() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"

            putExtra(Intent.EXTRA_TEXT, playlist!!.shareUrl)
            putExtra(Intent.EXTRA_TITLE, playlist!!.name)
        }

        application.startActivity(Intent.createChooser(shareIntent, null).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    fun play() {

    }

    fun shuffle() {

    }

    fun download() {

    }
}