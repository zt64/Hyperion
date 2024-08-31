package dev.zt64.hyperion.ui.util

import java.text.NumberFormat

object NumberFormatter {
    private val formatter by lazy {
        NumberFormat.getInstance()
    }

    fun formatLong(number: Number): String {
        return formatter.format(number)
    }

    fun formatCompact(number: Number): String {
        TODO()
    }
}