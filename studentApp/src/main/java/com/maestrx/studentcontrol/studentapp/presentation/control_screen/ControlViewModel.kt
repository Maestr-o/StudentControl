package com.maestrx.studentcontrol.studentapp.presentation.control_screen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ControlViewModel @Inject constructor() : ViewModel() {

    var state = mutableStateOf<ControlStatus>(ControlStatus.Default)
        private set

    fun onEvent(event: ControlEvent) {
        when (event) {
            is ControlEvent.SetScreenStatus -> {
                changeScreenStatus(event.status)
            }
        }
    }

    private fun changeScreenStatus(status: ControlStatus) {
        state.value = status
    }
}