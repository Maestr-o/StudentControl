package com.maestrx.studentcontrol.studentapp.presentation.loading_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maestrx.studentcontrol.studentapp.data.ServerInteractor
import com.maestrx.studentcontrol.studentapp.domain.model.Student
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoadingViewModel @Inject constructor(
    private val serverInteractor: ServerInteractor,
) : ViewModel(), ServerInteractor.StudentListCallback {

    var state by mutableStateOf(LoadingUiState())

    private var isDataExchangeStarted = false

    init {
        serverInteractor.studentListCallback = this

        serverInteractor.interState
            .onEach { status ->
                setScreenStatus(status)
            }
            .launchIn(viewModelScope)
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
            try {
                serverInteractor.dataExchange()
            } catch (e: Exception) {
                setScreenStatus(LoadingStatus.Error)
            }
        }
    }

    private fun setScreenStatus(status: LoadingStatus) {
        state = state.copy(screenState = status)
    }

    private fun setStudentId(id: Long) {
        setScreenStatus(LoadingStatus.Loading)
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
