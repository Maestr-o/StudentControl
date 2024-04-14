package com.maestrx.studentcontrol.studentapp.presentation.loading_screen

sealed interface LoadingState {
    data object Loading : LoadingState
    data object Input : LoadingState
    data object Error : LoadingState
    data object Success : LoadingState
    data object ReadyToBack : LoadingState
}