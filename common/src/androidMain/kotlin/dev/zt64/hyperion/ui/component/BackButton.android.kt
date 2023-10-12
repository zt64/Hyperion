package dev.zt64.hyperion.ui.component

import androidx.compose.runtime.Composable
import dev.olshevski.navigation.reimagined.pop
import dev.zt64.hyperion.ui.navigation.LocalNavController

@Composable
fun BackButton() {
    val navController = LocalNavController.current

    BackButton(onClick = navController::pop)
}