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
import com.maestrx.studentcontrol.teacherapp.repository.group.GroupRepository
import com.maestrx.studentcontrol.teacherapp.repository.lesson.LessonRepository
import com.maestrx.studentcontrol.teacherapp.repository.student.StudentRepository
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
    private val studentRepository: StudentRepository,
    private val groupRepository: GroupRepository,
    private val serverInteractor: ServerInteractor,
    @Assisted private val lesson: Lesson,
) : ViewModel() {

    private val _message = MutableStateFlow(Event(""))
    val message = _message.asStateFlow()

    private val _lessonState = MutableStateFlow(Lesson())
    val lessonState = _lessonState.asStateFlow()

    private val _controlStatus = MutableStateFlow<ControlStatus>(ControlStatus.NotReadyToStart)
    val controlStatus = _controlStatus.asStateFlow()

    private val _studentsWithGroupsState = MutableStateFlow(LessonDetailsUiState())
    val studentsWithGroupsState = _studentsWithGroupsState.asStateFlow()

    init {
        setLesson(lesson)

        viewModelScope.launch(Dispatchers.IO) {
            attendanceRepository.getByLessonId(lesson.id)
                .onEach { list ->
                    _studentsWithGroupsState.update {
                        it.copy(
                            attendance = list.map { attendance ->
                                Attendance.toData(attendance)
                            },
                            markedStudentsWithGroups = getMarkedStudentsWithGroups(),
                            notMarkedStudentsWithGroups = getNotMarkedStudentsWithGroups(),
                        )
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    private fun setLesson(lesson: Lesson) {
        groupRepository.getByLessonId(lesson.id)
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
                if (studentsWithGroupsState.value.totalStudentsCount == 0) {
                    _studentsWithGroupsState.update {
                        it.copy(totalStudentsCount = getTotalStudentsCount(lessonWithGroups.groups))
                    }
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
        val lastStatus = controlStatus.value
        _controlStatus.update { status }
        if (status is ControlStatus.Running && _lessonState.value.groups.isNotEmpty()) {
            startDataExchange()
        } else if (lastStatus is ControlStatus.Running && status !is ControlStatus.Running) {
            serverInteractor.closeSocket()
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

    private suspend fun getMarkedStudentsWithGroups(): List<Any> =
        viewModelScope.async(Dispatchers.Default) {
            val listOfStudents = async {
                studentRepository.getAttendedByLessonId(lessonState.value.id).map {
                    Student.fromResponseToData(it)
                }
            }
                .await()

            val attendedList = async {
                listOfStudents
                    .groupBy { it.group }
                    .map { (group, groupStudents) ->
                        AttendedInGroup(
                            name = group.name,
                            count = groupStudents.size,
                            max = studentRepository.getCountByGroupId(group.id),
                        )
                    }
            }

            val result = mutableListOf<Any>()
            val groupedStudents = listOfStudents.groupBy { it.group }
            for ((group, groupStudents) in groupedStudents) {
                result.addAll(attendedList.await().filter { it.name == group.name })
                result.addAll(groupStudents)
            }
            result
        }
            .await()

    private suspend fun getNotMarkedStudentsWithGroups(): List<Any> =
        viewModelScope.async(Dispatchers.Default) {
            val listOfStudents =
                studentRepository.getNotAttendedByLessonId(lessonState.value.id).map {
                    Student.fromResponseToData(it)
                }

            val attendedList = listOfStudents
                .groupBy { it.group }
                .map { (group) ->
                    Group(
                        id = group.id,
                        name = group.name,
                    )
                }

            val result = mutableListOf<Any>()
            val groupedStudents = listOfStudents.groupBy { it.group }
            for ((group, groupStudents) in groupedStudents) {
                result.addAll(attendedList.filter { it.name == group.name })
                result.addAll(groupStudents)
            }
            result
        }
            .await()

    private suspend fun getTotalStudentsCount(groups: List<Group>): Int =
        viewModelScope.async(Dispatchers.IO) {
            var count = 0
            groups.forEach { group ->
                count += studentRepository.getCountByGroupId(group.id)
            }
            count
        }
            .await()

    override fun onCleared() {
        super.onCleared()
        serverInteractor.closeSocket()
    }
}