package dev.zt64.hyperion.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.compose.stringResource
import dev.olshevski.navigation.reimagined.pop
import dev.zt64.hyperion.MR
import dev.zt64.hyperion.ui.navigation.LocalNavController

@Composable
fun BackButton() {
    val navController = LocalNavController.current

    IconButton(onClick = navController::pop) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = stringResource(MR.strings.back)
        )
    }
}

@Composable
fun BackButton(onClick: () -> Unit) {
    IconButton(onClick) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = stringResource(MR.strings.back)
        )
    }
}