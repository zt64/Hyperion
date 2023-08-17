package dev.zt64.hyperion

import androidx.compose.runtime.staticCompositionLocalOf
import dev.olshevski.navigation.reimagined.NavController
import dev.zt64.hyperion.ui.navigation.Destination

val LocalNavController = staticCompositionLocalOf<NavController<Destination>> {
    error("No local NavController provided")
}