package com.maestrx.studentcontrol.studentapp.presentation.control_screen

import com.maestrx.studentcontrol.studentapp.domain.model.PersonalData
import com.maestrx.studentcontrol.studentapp.domain.model.WifiState

data class ControlUiState(
    val personalData: PersonalData = PersonalData(),
    val wifiState: WifiState = WifiState.NotConnected,
)