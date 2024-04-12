package com.maestrx.studentcontrol.studentapp.presentation.loading_screen

sealed interface LoadingState {
    data object Loading : LoadingState
    data object Input : LoadingState
}