package dev.zt64.hyperion.ui

import android.app.PictureInPictureParams
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toAndroidRectF
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.toRect

/**
 * Potential alternative to mini-player UI component
 * On devices where Picture-in-Picture is supported, this activity can be used to display the
 * player in a floating window. Because this is separate from the main app, it can be used to
 * provide a mini-player experience.
 */
class PlayerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            finish()
            return
        }

        enterPictureInPictureMode(PictureInPictureParams.Builder().build())

        setContent {
            val context = LocalContext.current

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned {
                        val builder = PictureInPictureParams.Builder()
                        val sourceRect = it
                            .boundsInWindow()
                            .toAndroidRectF()
                            .toRect()
                        builder.setSourceRectHint(sourceRect)

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            builder.setAutoEnterEnabled(true)
                        }
                        context
                            .findActivity()
                            .setPictureInPictureParams(builder.build())
                    }
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
            }
        }
    }
}

internal fun Context.findActivity(): ComponentActivity {
    var context = this
    while (context is ContextWrapper) {
        if (context is ComponentActivity) return context
        context = context.baseContext
    }
    throw IllegalStateException("Picture in picture should be called in the context of an Activity")
}