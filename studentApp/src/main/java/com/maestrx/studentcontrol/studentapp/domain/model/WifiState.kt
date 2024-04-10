package com.maestrx.studentcontrol.studentapp.domain.model

sealed interface WifiState {
    data object Down : WifiState
    data object Loading : WifiState
    data object Idle : WifiState
}