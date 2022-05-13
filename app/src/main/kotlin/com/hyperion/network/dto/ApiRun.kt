package com.hyperion.network.dto

import kotlinx.serialization.Serializable

typealias TextRuns = List<TextRun>

@Serializable
data class TextRun(val text: String)

val TextRuns.value
    get() = first().text