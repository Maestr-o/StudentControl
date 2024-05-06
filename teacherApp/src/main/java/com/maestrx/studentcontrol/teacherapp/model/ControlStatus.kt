package com.maestrx.studentcontrol.teacherapp.model

sealed interface ControlStatus {
    data object Loading : ControlStatus
    data object NotReadyToStart : ControlStatus
    data object ReadyToStart : ControlStatus
    data object Running : ControlStatus
    data object Finished : ControlStatus
    data object Full : ControlStatus
}