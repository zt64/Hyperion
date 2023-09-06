package dev.zt64.hyperion.ui.navigation.util

import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.moveToTop
import dev.olshevski.navigation.reimagined.navigate

val <T> NavController<T>.currentDestination
    inline get() = backstack.entries.last().destination

@Suppress("NOTHING_TO_INLINE")
inline fun <T> NavController<T>.switchTo(destination: T) {
    if (!moveToTop { it == destination }) navigate(destination)
}