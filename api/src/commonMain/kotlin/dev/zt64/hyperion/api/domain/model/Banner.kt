package dev.zt64.hyperion.api.domain.model

import com.google.api.services.youtube.model.ImageSettings
import dev.zt64.hyperion.api.network.dto.ApiImageSource

data class Banner(val defaultBanner: ApiImageSource) {
    constructor(imageSettings: ImageSettings) : this(
        ApiImageSource(
            url = imageSettings.bannerExternalUrl + "=w1060-fcrop64=1,00005a57ffffa5a8-k-c0xffffffff-no-nd-rj",
            width = 1060,
            height = 175
        )
    )
}