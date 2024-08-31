package dev.zt64.hyperion

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.NavigatorDisposeBehavior
import cafe.adriel.voyager.transitions.ScreenTransition
import dev.zt64.hyperion.ui.ProvideSnackbarHostState
import dev.zt64.hyperion.ui.screen.BaseScreen
import dev.zt64.hyperion.ui.theme.HyperionTheme

/**
 * Parent composable for the entire Hyperion app.
 */
@Composable
fun Hyperion() {
    HyperionTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            val snackbarHostState = remember { SnackbarHostState() }

            val app = remember {
                movableContentOf {
                    Box {
                        val density = LocalDensity.current
                        Navigator(
                            screen = BaseScreen,
                            disposeBehavior = NavigatorDisposeBehavior(
                                disposeNestedNavigators = false,
                                disposeSteps = true
                            )
                        ) { navigator ->
                            ScreenTransition(
                                navigator = navigator,
                                transition = {
                                    MotionDefaults.sharedXAxisEnter(density) togetherWith
                                        MotionDefaults.sharedXAxisExit(density)
                                }
                            ) {
                                it.Content()
                            }
                        }

                        SnackbarHost(
                            modifier = Modifier.align(Alignment.BottomCenter),
                            hostState = snackbarHostState
                        )
                    }
                }
            }

            ProvideSnackbarHostState(snackbarHostState) {
                app()
            }
        }
    }
}

object SharedAxisDefaults {
    private const val ProgressThreshold = 0.3f

    private val Int.ForOutgoing: Int
        get() = (this * ProgressThreshold).toInt()

    private val Int.ForIncoming: Int
        get() = this - this.ForOutgoing

    const val SharedAxisOffset = 30.0
    private const val SharedAxisDuration = MotionTokens.DurationMedium2.toInt()

    private val sharedAxisSlideAnimationSpec = tween<IntOffset>(
        durationMillis = SharedAxisDuration,
        delayMillis = 0,
        easing = MotionTokens.EasingStandardCubicBezier
    )

    internal fun sharedXAxisEnterTransition(initialOffsetX: Int): EnterTransition {
        val slide = slideInHorizontally(
            animationSpec = sharedAxisSlideAnimationSpec,
            initialOffsetX = { initialOffsetX }
        )
        val fade = fadeIn(
            tween(
                durationMillis = SharedAxisDuration.ForIncoming,
                delayMillis = SharedAxisDuration.ForOutgoing,
                easing = MotionTokens.EasingStandardDecelerateCubicBezier
            )
        )
        return slide + fade
    }

    internal fun sharedXAxisExitTransition(targetOffsetX: Int): ExitTransition {
        val slide = slideOutHorizontally(
            animationSpec = sharedAxisSlideAnimationSpec,
            targetOffsetX = { targetOffsetX }
        )
        val fade = fadeOut(
            tween(
                durationMillis = SharedAxisDuration.ForOutgoing,
                delayMillis = 0,
                easing = MotionTokens.EasingStandardAccelerateCubicBezier
            )
        )
        return slide + fade
    }
}

object MotionDefaults {
    fun sharedXAxisEnter(density: Density): EnterTransition {
        val offsetPixels = with(density) { SharedAxisDefaults.SharedAxisOffset.dp.roundToPx() }
        return SharedAxisDefaults.sharedXAxisEnterTransition(offsetPixels)
    }

    fun sharedXAxisExit(density: Density): ExitTransition {
        val offsetPixels = with(density) { SharedAxisDefaults.SharedAxisOffset.dp.roundToPx() }
        return SharedAxisDefaults.sharedXAxisExitTransition(-offsetPixels)
    }

    fun sharedXAxisPopEnter(density: Density): EnterTransition {
        val offsetPixels = with(density) { SharedAxisDefaults.SharedAxisOffset.dp.roundToPx() }
        return SharedAxisDefaults.sharedXAxisEnterTransition(-offsetPixels)
    }

    fun sharedXAxisPopExit(density: Density): ExitTransition {
        val offsetPixels = with(density) { SharedAxisDefaults.SharedAxisOffset.dp.roundToPx() }
        return SharedAxisDefaults.sharedXAxisExitTransition(offsetPixels)
    }
}

object MotionTokens {
    const val DurationMedium2 = 300.0
    val EasingStandardCubicBezier = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
    val EasingStandardAccelerateCubicBezier = CubicBezierEasing(0.3f, 0.0f, 1.0f, 1.0f)
    val EasingStandardDecelerateCubicBezier = CubicBezierEasing(0.0f, 0.0f, 0.0f, 1.0f)
}