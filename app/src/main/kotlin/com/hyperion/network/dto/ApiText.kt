package com.hyperion.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiText(val runs: List<TextRun>) {
    val text = runs.joinToString(separator = "", transform = TextRun::text)

    @Serializable
    data class TextRun(val text: String)

    override fun toString() = text
}