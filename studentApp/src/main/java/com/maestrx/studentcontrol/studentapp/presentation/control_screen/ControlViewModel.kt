package com.maestrx.studentcontrol.studentapp.presentation.control_screen

import androidx.lifecycle.ViewModel
import com.maestrx.studentcontrol.studentapp.domain.model.PersonalData
import com.maestrx.studentcontrol.studentapp.domain.model.WifiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ControlViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(ControlUiState())
    val state = _state.asStateFlow()

    init {
        // load personal data from persistence
    }

    fun changeWifiState(network: String?) {
        _state.update {
            if (network != null) {
                it.copy(
                    wifiState = WifiState.Connected(network),
                )
            } else {
                it.copy(
                    wifiState = WifiState.NotConnected,
                )
            }
        }
    }

    fun setPersonalData(group: String, fullName: String) {
        _state.update {
            it.copy(personalData = PersonalData(group, fullName))
        }
    }
}