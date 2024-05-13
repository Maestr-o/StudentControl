package com.maestrx.studentcontrol.teacherapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maestrx.studentcontrol.teacherapp.db.entity.MarkEntity
import com.maestrx.studentcontrol.teacherapp.model.ControlStatus
import com.maestrx.studentcontrol.teacherapp.model.Group
import com.maestrx.studentcontrol.teacherapp.model.Lesson
import com.maestrx.studentcontrol.teacherapp.model.Mark
import com.maestrx.studentcontrol.teacherapp.model.MarkInGroup
import com.maestrx.studentcontrol.teacherapp.model.Student
import com.maestrx.studentcontrol.teacherapp.model.StudentMark
import com.maestrx.studentcontrol.teacherapp.repository.attendance.MarkRepository
import com.maestrx.studentcontrol.teacherapp.repository.lesson.LessonRepository
import com.maestrx.studentcontrol.teacherapp.repository.student.StudentRepository
import com.maestrx.studentcontrol.teacherapp.utils.Constants
import com.maestrx.studentcontrol.teacherapp.utils.Event
import com.maestrx.studentcontrol.teacherapp.viewmodel.di.ControlViewModelFactory
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
import kotlinx.coroutines.withContext

@HiltViewModel(assistedFactory = ControlViewModelFactory::class)
class ControlViewModel @AssistedInject constructor(
    private val markRepository: MarkRepository,
    private val lessonRepository: LessonRepository,
    private val studentRepository: StudentRepository,
    private val serverInteractor: ServerInteractor,
    @Assisted val lesson: Lesson,
) : ViewModel() {

    private val _message = MutableStateFlow(Event(""))
    val message = _message.asStateFlow()

    private val _controlStatus = MutableStateFlow<ControlStatus>(ControlStatus.Loading)
    val controlStatus = _controlStatus.asStateFlow()

    private val _studentsWithGroups = MutableStateFlow(ControlUiState())
    val studentsWithGroups = _studentsWithGroups.asStateFlow()

    private val _isManualMarkDialogShowed = MutableStateFlow(false)
    val isManualMarkDialogShowed = _isManualMarkDialogShowed.asStateFlow()

    private val _notMarkedStudentsWithGroups = MutableStateFlow<List<Any>>(mutableListOf())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _studentsWithGroups.update {
                it.copy(totalStudentsCount = getTotalStudentsCount(lesson.groups))
            }
            markRepository.getByLessonId(lesson.id)
                .onEach { list ->
                    _studentsWithGroups.update {
                        it.copy(
                            marks = list.map { mark ->
                                Mark.toData(mark)
                            },
                            markedStudentsWithGroups = getMarkedStudentsWithGroups(),
                        )
                    }
                }
                .launchIn(viewModelScope)
        }

        isManualMarkDialogShowed.onEach { _ ->
            setNotMarkedStudentsWithGroups(getNotMarkedStudentsWithGroups())
        }
            .launchIn(viewModelScope)
    }

    fun addMarks(list: List<Any>) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val marks = list.filterIsInstance<StudentMark>().filter { it.isAttended }
                val entities = marks.map { mark ->
                    MarkEntity(lessonId = lesson.id, studentId = mark.id)
                }
                withContext(Dispatchers.IO) {
                    markRepository.saveList(entities)
                }
            } catch (e: Exception) {
                _message.value = Event(Constants.MESSAGE_ERROR_SAVING_MARKS)
                Log.d(Constants.DEBUG_TAG, "Error saving marks: ${e.printStackTrace()}")
            }
        }
    }

    fun deleteLesson() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                lessonRepository.deleteById(lesson.id)
            } catch (e: Exception) {
                _message.value = Event(Constants.MESSAGE_ERROR_DELETING_LESSON)
                Log.d(Constants.DEBUG_TAG, "Error deleting lesson: ${e.printStackTrace()}")
            }
        }
    }

    fun setControlStatus(status: ControlStatus) {
        val lastStatus = controlStatus.value
        _controlStatus.update { status }
        if (status is ControlStatus.Running && lesson.groups.isNotEmpty()) {
            startDataExchange()
        } else if ((lastStatus is ControlStatus.Running && status !is ControlStatus.Running)) {
            serverInteractor.closeSocket()
        }
    }

    private fun startDataExchange() {
        viewModelScope.launch {
            try {
                serverInteractor.dataExchange(lesson)
            } catch (e: Exception) {
                _controlStatus.update { ControlStatus.ReadyToStart }
            }
        }
    }

    private suspend fun getMarkedStudentsWithGroups(): List<Any> = withContext(Dispatchers.IO) {
        val listOfStudents = studentRepository.getAttendedByLessonId(lesson.id).map {
            Student.fromResponseToData(it)
        }

        val groupList = async(Dispatchers.Default) {
            listOfStudents
                .groupBy { it.group }
                .map { (group, groupStudents) ->
                    MarkInGroup(
                        name = group.name,
                        count = groupStudents.size,
                        max = studentRepository.getCountByGroupId(group.id),
                    )
                }
        }

        val result = mutableListOf<Any>()
        val groupedStudents = listOfStudents.groupBy { it.group }
        for ((group, groupStudents) in groupedStudents) {
            result.addAll(groupList.await().filter { it.name == group.name })
            result.addAll(groupStudents)
        }
        result
    }

    private suspend fun getNotMarkedStudentsWithGroups(): List<Any> = withContext(Dispatchers.IO) {
        val listOfStudents =
            studentRepository.getNotAttendedByLessonId(lesson.id).map {
                Student.fromResponseToData(it)
            }

        val groupList = async(Dispatchers.Default) {
            listOfStudents
                .groupBy { it.group }
                .map { (group) -> group }
        }

        val result = mutableListOf<Any>()
        val groupedStudents = listOfStudents.groupBy { it.group }
        for ((group, groupStudents) in groupedStudents) {
            result.addAll(groupList.await().filter { it.name == group.name })
            result.addAll(groupStudents)
        }
        result
    }

    private suspend fun getTotalStudentsCount(groups: List<Group>): Int =
        viewModelScope.async(Dispatchers.IO) {
            var count = 0
            groups.forEach { group ->
                count += studentRepository.getCountByGroupId(group.id)
            }
            count
        }
            .await()

    fun setMarkDialogShow(state: Boolean) {
        _isManualMarkDialogShowed.update { state }
    }

    fun setNotMarkedStudentsWithGroups(list: List<Any>) {
        _notMarkedStudentsWithGroups.update { list }
    }

    fun updateNotMarkedStudentsWithGroups() {
        viewModelScope.launch {
            setNotMarkedStudentsWithGroups(getNotMarkedStudentsWithGroups())
            _message.value = Event(Constants.MESSAGE_SHOW_MARK_DIALOG)
        }
    }

    fun getMarkList(): List<Any> =
        _notMarkedStudentsWithGroups.value.map { item ->
            when (item) {
                is Student -> StudentMark(item.id, item.fullName)
                is StudentMark -> item
                is Group -> item
                else -> throw IllegalStateException("Type error")
            }
        }

    override fun onCleared() {
        super.onCleared()
        serverInteractor.closeSocket()
    }
}