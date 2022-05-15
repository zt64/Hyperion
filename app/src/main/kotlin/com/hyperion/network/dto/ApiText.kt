package com.hyperion.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiText(val runs: List<TextRun>) {
    @Serializable
    data class TextRun(val text: String)

    override fun toString() = runs.first().text
}