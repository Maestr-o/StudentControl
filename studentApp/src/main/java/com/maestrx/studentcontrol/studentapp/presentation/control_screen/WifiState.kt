package com.maestrx.studentcontrol.studentapp.presentation.control_screen

sealed interface WifiState {
    data object NotConnected : WifiState
    data object Connected : WifiState
}