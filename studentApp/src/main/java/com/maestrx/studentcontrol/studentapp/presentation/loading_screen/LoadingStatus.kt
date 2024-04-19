package com.maestrx.studentcontrol.studentapp.presentation.loading_screen

sealed interface LoadingStatus {
    data object Loading : LoadingStatus
    data object Input : LoadingStatus
    data object Error : LoadingStatus
    data object Success : LoadingStatus
    data class ReadyToBack(val isConnected: Boolean) : LoadingStatus
}