package com.hyperion.ui.viewmodel

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import com.hyperion.domain.manager.PreferencesManager
import com.zt.innertube.domain.repository.InnerTubeRepository

@Stable
class FeedViewModel(
    private val preferencesManager: PreferencesManager,
    private val innerTube: InnerTubeRepository
) : ViewModel() {

}