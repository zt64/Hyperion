package com.hyperion.ui.viewmodel

import android.app.Application
import android.content.Intent
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.hyperion.R
import com.hyperion.domain.model.DomainPlaylist
import com.hyperion.domain.model.DomainVideoPartial
import com.hyperion.domain.repository.InnerTubeRepository
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val application: Application,
    private val repository: InnerTubeRepository
) : ViewModel() {
    sealed interface State {
        object Loaded : State
        object Loading : State
        class Error(val exception: Exception) : State
    }

    var state by mutableStateOf<State>(State.Loading)
        private set
    var playlist by mutableStateOf<DomainPlaylist?>(null)
        private set
    var videos by mutableStateOf(emptyFlow<PagingData<DomainVideoPartial>>())
        private set
    val snackbarHostState = SnackbarHostState()

    fun getPlaylist(id: String) {
        state = State.Loading

        viewModelScope.launch {
            try {
                playlist = repository.getPlaylist(id)
                videos = Pager(PagingConfig(4)) {
                    object : PagingSource<String, DomainVideoPartial>() {
                        override suspend fun load(params: LoadParams<String>) = try {
                            val response = if (params.key == null) {
                                playlist!!.content
                            } else {
                                repository.getPlaylist(id, params.key!!)
                            }

                            LoadResult.Page(
                                data = response.videos,
                                prevKey = null,
                                nextKey = response.continuation
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()

                            LoadResult.Error(e)
                        }

                        override fun getRefreshKey(state: PagingState<String, DomainVideoPartial>): String? = null
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
}