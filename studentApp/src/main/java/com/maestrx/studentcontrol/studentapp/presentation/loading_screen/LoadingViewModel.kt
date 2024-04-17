package com.maestrx.studentcontrol.studentapp.presentation.loading_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maestrx.studentcontrol.studentapp.domain.model.Student
import com.maestrx.studentcontrol.studentapp.domain.wifi.ServerInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoadingViewModel @Inject constructor(
    private val serverInteractor: ServerInteractor,
) : ViewModel(), ServerInteractor.StudentListCallback {

    var state by mutableStateOf(LoadingUiState())
        private set

    private var isDataExchangeStarted = false

    init {
        serverInteractor.studentListCallback = this
    }

    fun onEvent(event: LoadingEvent) {
        when (event) {
            is LoadingEvent.StartDataExchange -> startDataExchange()
            is LoadingEvent.SetScreenStatus -> setScreenStatus(event.status)
            is LoadingEvent.SetStudentId -> setStudentId(event.id)
        }
    }

    private fun startDataExchange() {
        if (isDataExchangeStarted) return
        isDataExchangeStarted = true
        viewModelScope.launch {
            state = try {
                serverInteractor.dataExchange()
                state.copy(screenState = LoadingStatus.Success)
            } catch (e: Exception) {
                state.copy(screenState = LoadingStatus.Error)
            }
        }
    }

    private fun setScreenStatus(status: LoadingStatus) {
        state = state.copy(screenState = status)
    }

    private fun setStudentId(id: Long) {
        serverInteractor.stId = id
    }

    override fun onStudentsReceived(students: List<Student>) {
        state = LoadingUiState(
            screenState = LoadingStatus.Input,
            students = students,
        )
    }

    override fun onCleared() {
        super.onCleared()
        serverInteractor.closeSocket()
    }
}
