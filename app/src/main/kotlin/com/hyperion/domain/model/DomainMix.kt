package com.hyperion.domain.model

data class DomainMix(
    val id: String,
    val title: String,
    val subtitle: String,
    val thumbnailUrl: String
)

data class DomainMixPartial(
    val id: String,
    val title: String,
    val subtitle: String,
    val thumbnailUrl: String
) : Entity