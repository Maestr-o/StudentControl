package com.maestrx.studentcontrol.studentapp

sealed class Screen(val route: String) {
    data object Permissions : Screen("permissions")
    data object Control : Screen("control")
    data object Loading : Screen("loading")
}