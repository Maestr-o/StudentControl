package com.maestrx.studentcontrol.studentapp.presentation.loading_screen

sealed interface LoadingEvent {
    data object StartDataExchange : LoadingEvent
    data class SetScreenStatus(val status: LoadingStatus) : LoadingEvent
    data class SetStudentId(val id: Long) : LoadingEvent
}