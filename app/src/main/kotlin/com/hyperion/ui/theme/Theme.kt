/*
 * Copyright (c) 2022 Juby210 & zt
 * Licensed under the Open Software License version 3.0
 */

package com.hyperion.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hyperion.R

private val LightColorScheme = lightColorScheme()
private val DarkColorScheme = darkColorScheme()
private val BlackColorScheme = darkColorScheme(
    background = Color.Black,
    surface = Color.Black,
    primary = Color.LightGray,
    onPrimary = Color.DarkGray,
    secondary = Color.Gray,
    onSecondary = Color.LightGray,
    secondaryContainer = Color.DarkGray,
    onSecondaryContainer = Color.White,
    outline = Color.LightGray
)

@Composable
fun HyperionTheme(
    isBlack: Boolean = false,
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    isDynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val dynamicColor = isDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val colorScheme = when {
        dynamicColor && isDarkTheme -> dynamicDarkColorScheme(LocalContext.current)
        dynamicColor && !isDarkTheme -> dynamicLightColorScheme(LocalContext.current)
        isDarkTheme -> if (isBlack) BlackColorScheme else DarkColorScheme
        else -> LightColorScheme
    }

    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = colorScheme.background,
            darkIcons = !isDarkTheme
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

enum class Theme {
    SYSTEM,
    LIGHT,
    DARK;

    @Composable
    fun toDisplayName(): String = when (this) {
        SYSTEM -> stringResource(R.string.theme_system)
        LIGHT -> stringResource(R.string.theme_light)
        DARK -> stringResource(R.string.theme_dark)
    }
}