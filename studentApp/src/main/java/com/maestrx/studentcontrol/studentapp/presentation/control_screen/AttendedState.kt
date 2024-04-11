package com.maestrx.studentcontrol.studentapp.presentation.control_screen

sealed interface AttendedState {
    data object Idle : AttendedState
    data object Success : AttendedState
    data object Error : AttendedState
}