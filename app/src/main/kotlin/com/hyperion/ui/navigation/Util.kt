package com.hyperion.ui.navigation

import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.moveToTop
import dev.olshevski.navigation.reimagined.navigate

val <T> NavController<T>.currentDestination
    get() = backstack.entries.last().destination

fun <T> NavController<T>.switchTo(destination: T) {
    if (!moveToTop { it == destination }) navigate(destination)
}