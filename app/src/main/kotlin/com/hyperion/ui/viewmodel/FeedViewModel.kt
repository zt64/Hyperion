package com.hyperion.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.hyperion.domain.manager.PreferencesManager
import com.zt.innertube.domain.repository.InnerTubeRepository

class FeedViewModel(
    private val preferencesManager: PreferencesManager,
    private val innerTube: InnerTubeRepository
) : ViewModel() {

}