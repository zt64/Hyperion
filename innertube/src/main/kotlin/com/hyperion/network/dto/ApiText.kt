package com.hyperion.network.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class ApiText(private val runs: List<TextRun>) {
    private val text = runs.joinToString(separator = "", transform = TextRun::text)

    @Serializable
    data class TextRun(val text: String)

    override fun toString() = text
}