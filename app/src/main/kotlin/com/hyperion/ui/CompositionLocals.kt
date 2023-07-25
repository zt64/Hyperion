package com.hyperion.ui

import androidx.compose.runtime.staticCompositionLocalOf
import com.hyperion.ui.navigation.Destination
import dev.olshevski.navigation.reimagined.NavController

val LocalNavController = staticCompositionLocalOf<NavController<Destination>> {
    error("No local NavController provided")
}