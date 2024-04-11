package com.maestrx.studentcontrol.studentapp.presentation.control_screen

data class ControlUiState(
    val wifiState: WifiState = WifiState.NotConnected,
    val attendedState: AttendedState = AttendedState.Idle,
)