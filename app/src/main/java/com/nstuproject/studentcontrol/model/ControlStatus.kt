package com.nstuproject.studentcontrol.model

sealed interface ControlStatus {
    data object NotReadyToStart : ControlStatus
    data object ReadyToStart : ControlStatus
    data object Starting : ControlStatus
    data object Running : ControlStatus
    data object Stopping : ControlStatus
    data object Finished : ControlStatus
}