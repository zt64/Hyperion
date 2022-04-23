package com.hyperion.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hyperion.R
import com.hyperion.preferences.Prefs
import com.ramcosta.composedestinations.annotation.Destination

@Destination(
    start = true,
    navGraph = "intro"
)
@Composable
fun IntroScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = null
        )

        Text(
            text = stringResource(R.string.app_welcome),
            style = MaterialTheme.typography.bodyLarge
        )

        Column(
            modifier = Modifier.width(IntrinsicSize.Max)
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                onClick = { /*TODO*/ }
            ) {
                Text(stringResource(R.string.login_google))
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { Prefs.firstLaunch = false }
            ) {
                Text(stringResource(R.string.login_skip))
            }
        }
    }
}