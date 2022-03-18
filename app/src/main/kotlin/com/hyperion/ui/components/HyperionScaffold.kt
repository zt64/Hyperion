package com.hyperion.ui.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hyperion.ui.screens.NavGraphs
import com.hyperion.ui.screens.destinations.HomeScreenDestination
import com.hyperion.ui.screens.navDestination
import com.ramcosta.composedestinations.DestinationsNavHost

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalAnimationApi::class,
)
@Composable
fun HyperionScaffold() {
    val navController = rememberNavController()
    val currentDestination = navController.currentBackStackEntryAsState().value?.navDestination ?: HomeScreenDestination
    var search by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopBar(
                navController = navController,
                search = search,
                onValueChange = { search = it }
            )
        },
        bottomBar = { BottomBar(navController) }
    ) {
        DestinationsNavHost(
            modifier = Modifier.padding(it),
//                .padding(horizontal = 14.dp, vertical = 8.dp),
            navController = navController,
//            engine = navHostEngine,
            navGraph = NavGraphs.root
        )
    }
}