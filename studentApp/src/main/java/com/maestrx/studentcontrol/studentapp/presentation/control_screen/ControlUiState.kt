package com.maestrx.studentcontrol.studentapp.presentation.control_screen

import com.maestrx.studentcontrol.studentapp.domain.model.PersonalData
import com.maestrx.studentcontrol.studentapp.domain.model.WifiState

data class ControlUiState(
    val networks: List<String> = emptyList(),
    val personalData: PersonalData = PersonalData(),
    val wifiState: WifiState = WifiState.Idle,
    val currentWifi: String = "",
)