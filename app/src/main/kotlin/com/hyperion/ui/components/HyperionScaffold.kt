package com.hyperion.ui.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.hyperion.ui.screens.NavGraphs
import com.ramcosta.composedestinations.DestinationsNavHost

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalAnimationApi::class,
)
@Composable
fun HyperionScaffold() {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopBar(
                navController = navController
            )
        },
        bottomBar = { BottomBar(navController) }
    ) { paddingValues ->
        DestinationsNavHost(
            modifier = Modifier.padding(paddingValues),
            navController = navController,
            navGraph = NavGraphs.root
        )
    }
}