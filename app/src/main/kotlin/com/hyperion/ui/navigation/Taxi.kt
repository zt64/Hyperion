package com.hyperion.ui.navigation

import android.os.Parcelable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

interface Destination : Parcelable

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun <T : Destination> Taxi(
    navigator: Navigator<T>,
    transitionSpec: AnimatedContentScope<T>.() -> ContentTransform,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit
) {
    val saveableStateHolder = rememberSaveableStateHolder()
    val currentDestination = navigator.currentDestination
    val transition = updateTransition(currentDestination, label = "")

//    LaunchedEffect(Unit) {
//        snapshotFlow(transition::currentState).collect { currentState ->
//            if (currentDestination == currentState) {
//                saveableStateHolder.removeState(currentState)
//            }
//        }
//    }

    transition.AnimatedContent(
        modifier = modifier,
        transitionSpec = transitionSpec,
        contentAlignment = Alignment.Center
    ) { dest ->
        saveableStateHolder.SaveableStateProvider(dest) {
            content(dest)
        }
    }
}