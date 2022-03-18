package com.hyperion.ui.screens

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomDrawerValue
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavController
import com.hyperion.ui.components.VideoList
import com.ramcosta.composedestinations.annotation.Destination
import kotlin.math.max
import kotlin.math.roundToInt

enum class PlayerValue { Minimized, Full }

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.material.ExperimentalMaterialApi::class)
@Destination(
    start = true
)
@Composable
fun HomeScreen(
    navController: NavController
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        VideoList(navController = navController)

        val fullHeight = constraints.maxHeight.toFloat()
        var playerHeight by remember(fullHeight) { mutableStateOf(fullHeight) }
        val isLandscape = constraints.maxWidth > constraints.maxHeight

        val peekHeight = fullHeight * 0.2f
        val minHeight = 0f
        val expandedHeight = max(minHeight, fullHeight - playerHeight)
        val anchors = if (playerHeight < peekHeight || isLandscape) {
            mapOf(
                fullHeight to BottomDrawerValue.Closed,
                expandedHeight to BottomDrawerValue.Expanded
            )
        } else {
            mapOf(
                fullHeight to BottomDrawerValue.Closed,
                peekHeight to BottomDrawerValue.Open,
                expandedHeight to BottomDrawerValue.Expanded
            )
        }

        val swipeableState = rememberSwipeableState(
            initialValue = BottomDrawerValue.Closed,
            animationSpec = tween(
                durationMillis = 600,
                easing = LinearOutSlowInEasing
            )
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .offset { IntOffset(x = 0, y = swipeableState.offset.value.roundToInt()) }
                .onGloballyPositioned { position ->
                    playerHeight = position.size.height.toFloat()
                }
                .background(MaterialTheme.colorScheme.tertiaryContainer)
                .swipeable(
                    state = swipeableState,
                    anchors = anchors,
                    thresholds = { _, _ -> FractionalThreshold(0.3f) },
                    orientation = Orientation.Vertical
                )
        )
    }
}