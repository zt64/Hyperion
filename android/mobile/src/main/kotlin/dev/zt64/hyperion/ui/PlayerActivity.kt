package dev.zt64.hyperion.ui

import android.os.Bundle
import androidx.activity.ComponentActivity

/**
 * Potential alternative to mini-player UI component
 * On devices where Picture-in-Picture is supported, this activity can be used to display the
 * player in a floating window. Because this is separate from the main app, it can be used to
 * provide a mini-player experience.
 */
class PlayerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}