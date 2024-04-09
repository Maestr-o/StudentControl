package com.maestrx.studentcontrol.teacherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maestrx.studentcontrol.teacherapp.model.ControlStatus
import com.maestrx.studentcontrol.teacherapp.model.Group
import com.maestrx.studentcontrol.teacherapp.model.Lesson
import com.maestrx.studentcontrol.teacherapp.repository.lesson.LessonRepository
import com.maestrx.studentcontrol.teacherapp.repository.lessonGroupCrossRef.LessonGroupCrossRefRepository
import com.maestrx.studentcontrol.teacherapp.viewmodel.di.LessonDetailsViewModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

@HiltViewModel(assistedFactory = LessonDetailsViewModelFactory::class)
class LessonDetailsViewModel @AssistedInject constructor(
    private val lessonRepository: LessonRepository,
    private val lessonGroupCrossRefRepository: LessonGroupCrossRefRepository,
    @Assisted lesson: Lesson,
) : ViewModel() {

    private val _lessonState = MutableStateFlow(Lesson())
    val lessonState = _lessonState.asStateFlow()

    private val _controlStatus = MutableStateFlow<ControlStatus>(ControlStatus.NotReadyToStart)
    val controlStatus = _controlStatus.asStateFlow()

    init {
        setLesson(lesson)
    }

    private fun setLesson(lesson: Lesson) {
        lessonGroupCrossRefRepository.getGroupsByLesson(lesson.id)
            .onEach { groups ->
                val lessonWithGroups =
                    lesson.copy(
                        groups = groups.map {
                            Group.toData(it)
                        }
                            .sortedBy {
                                it.name
                            }
                    )
                _lessonState.update { lessonWithGroups }
            }
            .launchIn(viewModelScope)
    }

    fun deleteLesson() {
        viewModelScope.launch {
            lessonRepository.deleteById(lessonState.value.id)
        }
    }

    fun setControlStatus(status: ControlStatus) {
        _controlStatus.update { status }
    }

    fun sendData(message: String, ipAddress: String, port: Int) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val sendData = message.toByteArray()
                val address = InetAddress.getByName(ipAddress)
                val packet = DatagramPacket(sendData, sendData.size, address, port)

                val socket = DatagramSocket()
                socket.send(packet)
                socket.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}