package com.maestrx.studentcontrol.studentapp.domain.model

sealed interface WifiState {
    data object NotConnected : WifiState
    data class Connected(val network: String) : WifiState
}