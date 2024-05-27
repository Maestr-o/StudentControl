package com.maestrx.studentcontrol.studentapp

sealed class Screen(val route: String) {
    data object Settings : Screen("settings")
    data object Control : Screen("control")
    data object Loading : Screen("loading")
}