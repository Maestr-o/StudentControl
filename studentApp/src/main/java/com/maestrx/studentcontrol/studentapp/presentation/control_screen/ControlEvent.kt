package com.maestrx.studentcontrol.studentapp.presentation.control_screen

import android.net.wifi.ScanResult

interface ControlEvent {
    data class SetScreenStatus(val status: ControlStatus) : ControlEvent
    data class SelectNetwork(val network: ScanResult?) : ControlEvent
    data class UpdatePassword(val pass: String) : ControlEvent
}