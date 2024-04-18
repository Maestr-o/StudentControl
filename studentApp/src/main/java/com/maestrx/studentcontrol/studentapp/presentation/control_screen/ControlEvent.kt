package com.maestrx.studentcontrol.studentapp.presentation.control_screen

interface ControlEvent {
    data class SetScreenStatus(val status: ControlStatus) : ControlEvent
}