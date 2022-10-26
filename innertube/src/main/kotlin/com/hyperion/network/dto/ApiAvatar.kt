package com.hyperion.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiAvatar(
    val endpoint: OnTap<ApiNavigationEndpoint>,
    val image: ApiImage
)

@Serializable
data class DecoratedAvatar(val avatar: ImageContainer)