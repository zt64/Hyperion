package dev.zt64.hyperion.ui.viewmodel

import androidx.lifecycle.ViewModel
import dev.zt64.innertube.domain.repository.InnerTubeRepository

class LibraryViewModel(
    private val innerTube: InnerTubeRepository
) : ViewModel() {

}