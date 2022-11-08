package com.hyperion.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.zt.innertube.domain.repository.InnerTubeRepository

class ChannelsViewModel(
    private val innerTube: InnerTubeRepository
) : ViewModel() {

}