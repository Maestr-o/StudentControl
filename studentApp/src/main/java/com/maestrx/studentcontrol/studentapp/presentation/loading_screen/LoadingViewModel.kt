package com.maestrx.studentcontrol.studentapp.presentation.loading_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maestrx.studentcontrol.studentapp.domain.wifi.ServerInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoadingViewModel @Inject constructor(
    private val serverInteractor: ServerInteractor,
) : ViewModel() {

    private val _state = MutableStateFlow(LoadingUiState())
    val state = _state.asStateFlow()

    private var isDataExchangeStarted = false

    fun startDataExchange() {
        if (isDataExchangeStarted) return
        isDataExchangeStarted = true
        viewModelScope.launch {
            try {
                serverInteractor.dataExchange()
                _state.update {
                    it.copy(screenState = LoadingState.Success)
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(screenState = LoadingState.Error)
                }
            }
        }
    }

    fun setScreenStatus(status: LoadingState) {
        _state.update {
            it.copy(screenState = status)
        }
    }

    override fun onCleared() {
        super.onCleared()
        serverInteractor.closeSocket()
    }
}