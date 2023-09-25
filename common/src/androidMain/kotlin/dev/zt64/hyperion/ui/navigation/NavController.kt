package dev.zt64.hyperion.ui.navigation

import androidx.compose.runtime.staticCompositionLocalOf
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.navController

val LocalNavController = staticCompositionLocalOf<NavController<Destination>> {
    // error("No local NavController provided")
    navController(emptyList())
}