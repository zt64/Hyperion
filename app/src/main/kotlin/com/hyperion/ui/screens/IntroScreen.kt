package com.hyperion.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.hyperion.preferences.Prefs
import com.ramcosta.composedestinations.annotation.Destination

@Destination(
    start = true,
    navGraph = "intro"
)
@Composable
fun IntroScreen() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { Prefs.firstLaunch = false }) {
            Text("Ok")
        }
    }
}