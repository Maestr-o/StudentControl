package com.maestrx.studentcontrol.studentapp.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val Primary = Color(0xFF039BE5)
val Connected = Color(0xFF29B6F6)

val DarkThemePalette = darkColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    onBackground = Color.White,
)

val LightThemePalette = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    onBackground = Color.Black,
)