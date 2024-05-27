package com.maestrx.studentcontrol.studentapp.presentation.control_screen

sealed interface ControlStatus {
    data object WifiIsDown : ControlStatus
    data object WifiIsUp : ControlStatus
    data object Connected : ControlStatus
    data object Completed : ControlStatus
}