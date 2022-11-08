package com.hyperion.ui.navigation

import dev.olshevski.navigation.reimagined.NavController

val <T> NavController<T>.currentDestination
    get() = backstack.entries.last().destination