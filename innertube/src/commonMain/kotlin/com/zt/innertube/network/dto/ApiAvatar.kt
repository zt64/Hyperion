package com.zt.innertube.network.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class ApiAvatar(
    val endpoint: OnTap<ApiNavigationEndpoint>,
    val image: ApiImage
)

@Serializable
internal data class DecoratedAvatar(val avatar: ImageContainer)