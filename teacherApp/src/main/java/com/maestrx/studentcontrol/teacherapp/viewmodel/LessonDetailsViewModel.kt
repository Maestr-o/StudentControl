package com.maestrx.studentcontrol.teacherapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maestrx.studentcontrol.teacherapp.model.Attendance
import com.maestrx.studentcontrol.teacherapp.model.AttendedInGroup
import com.maestrx.studentcontrol.teacherapp.model.ControlStatus
import com.maestrx.studentcontrol.teacherapp.model.Group
import com.maestrx.studentcontrol.teacherapp.model.Lesson
import com.maestrx.studentcontrol.teacherapp.model.Student
import com.maestrx.studentcontrol.teacherapp.repository.attendance.AttendanceRepository
import com.maestrx.studentcontrol.teacherapp.repository.lesson.LessonRepository
import com.maestrx.studentcontrol.teacherapp.repository.lesson_group_cross_ref.LessonGroupCrossRefRepository
import com.maestrx.studentcontrol.teacherapp.utils.Constants
import com.maestrx.studentcontrol.teacherapp.utils.Event
import com.maestrx.studentcontrol.teacherapp.viewmodel.di.LessonDetailsViewModelFactory
import com.maestrx.studentcontrol.teacherapp.wifi.ServerInteractor
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = LessonDetailsViewModelFactory::class)
class LessonDetailsViewModel @AssistedInject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val lessonRepository: LessonRepository,
    private val lessonGroupCrossRefRepository: LessonGroupCrossRefRepository,
    private val serverInteractor: ServerInteractor,
    @Assisted private val lesson: Lesson,
) : ViewModel() {

    private val _message = MutableStateFlow(Event(""))
    val message = _message.asStateFlow()

    private val _lessonState = MutableStateFlow(Lesson())
    val lessonState = _lessonState.asStateFlow()

    private val _controlStatus = MutableStateFlow<ControlStatus>(ControlStatus.NotReadyToStart)
    val controlStatus = _controlStatus.asStateFlow()

    private val _studentsWithGroups = MutableStateFlow(LessonDetailsUiState())
    val studentsWithGroups = _studentsWithGroups.asStateFlow()

    init {
        setLesson(lesson)

        viewModelScope.launch {
            attendanceRepository.getByLesson(lesson.id)
                .onEach { list ->
                    _studentsWithGroups.update {
                        LessonDetailsUiState(
                            attendance = list.map {
                                Attendance.toData(it)
                            },
                            studentsWithGroups = getStudentsWithGroups(getListOfStudents()),
                        )
                    }
                }
                .launchIn(viewModelScope)
        }
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
                _lessonState.update {
                    lessonWithGroups
                }
                setControlStatus(controlStatus.value)
            }
            .launchIn(viewModelScope)
    }

    fun deleteLesson() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                lessonRepository.deleteById(lesson.id)
            } catch (e: Exception) {
                _message.value = Event(Constants.MESSAGE_ERROR_DELETING_LESSON)
                Log.d(Constants.DEBUG_TAG, "Error deleting lesson: $e")
            }
        }
    }

    fun setControlStatus(status: ControlStatus) {
        _controlStatus.update { status }
        if (status is ControlStatus.Running && _lessonState.value.groups.isNotEmpty()) {
            startDataExchange()
        }
    }

    private fun startDataExchange() {
        viewModelScope.launch {
            try {
                serverInteractor.dataExchange(lessonState.value)
            } catch (e: Exception) {
                _controlStatus.update { ControlStatus.ReadyToStart }
            }
        }
    }

    private suspend fun getListOfStudents(): List<Student> =
        viewModelScope.async {
            lessonRepository.getStudentsByLessonId(lessonState.value.id).map {
                Student.fromResponseToData(it)
            }
        }
            .await()

    private fun getStudentsWithGroups(listOfStudents: List<Student>): List<Any> {
        val attendedList = listOfStudents
            .groupBy { it.group }
            .map { (group, groupStudents) ->
                AttendedInGroup(group.name, groupStudents.size)
            }

        val result = mutableListOf<Any>()
        val groupedStudents = listOfStudents.groupBy { it.group }
        for ((group, groupStudents) in groupedStudents) {
            result.addAll(attendedList.filter { it.name == group.name })
            result.addAll(groupStudents)
        }
        return result
    }

    override fun onCleared() {
        super.onCleared()
        serverInteractor.closeSocket()
    }
}