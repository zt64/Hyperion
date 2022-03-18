package com.hyperion.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hyperion.R
import com.hyperion.ui.screens.destinations.HomeScreenDestination
import com.hyperion.ui.screens.destinations.PlayerScreenDestination
import com.hyperion.ui.screens.navDestination

@Composable
fun TopBar(
    navController: NavController,
    search: String,
    onValueChange: (String) -> Unit
) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.navDestination ?: HomeScreenDestination
    val focusManager = LocalFocusManager.current

    AnimatedVisibility(
        visible = currentDestination != PlayerScreenDestination,
        enter = expandVertically(expandFrom = Alignment.Top),
        exit = shrinkVertically(shrinkTowards = Alignment.Top)
    ) {
        SmallTopAppBar(
            title = {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 2.dp),
                    value = search,
                    label = {
                        Text(
                            text = stringResource(R.string.search),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    trailingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    onValueChange = onValueChange
                )
            }
        )
    }
}