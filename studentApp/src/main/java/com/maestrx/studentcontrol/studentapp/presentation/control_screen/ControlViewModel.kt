package com.maestrx.studentcontrol.studentapp.presentation.control_screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ControlViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(ControlUiState())
    val state = _state.asStateFlow()

    fun changeWifiState(st: Boolean) {
        _state.update {
            if (st) {
                it.copy(wifiState = WifiState.Connected)
            } else {
                it.copy(wifiState = WifiState.NotConnected)
            }
        }
    }
}