package com.hyperion.ui.viewmodel

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import com.zt.innertube.domain.repository.InnerTubeRepository

@Stable
class LibraryViewModel(
    private val innerTube: InnerTubeRepository
) : ViewModel() {

}