package com.maestrx.studentcontrol.studentapp.presentation.control_screen

sealed interface ControlStatus {
    data object NotConnected : ControlStatus
    data object Connected : ControlStatus
}