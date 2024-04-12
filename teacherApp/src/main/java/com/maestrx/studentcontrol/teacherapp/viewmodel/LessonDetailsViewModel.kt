package com.maestrx.studentcontrol.teacherapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maestrx.studentcontrol.teacherapp.db.entity.AttendanceEntity
import com.maestrx.studentcontrol.teacherapp.model.Attendance
import com.maestrx.studentcontrol.teacherapp.model.ControlStatus
import com.maestrx.studentcontrol.teacherapp.model.Group
import com.maestrx.studentcontrol.teacherapp.model.Lesson
import com.maestrx.studentcontrol.teacherapp.repository.attendance.AttendanceRepository
import com.maestrx.studentcontrol.teacherapp.repository.lesson.LessonRepository
import com.maestrx.studentcontrol.teacherapp.repository.lessonGroupCrossRef.LessonGroupCrossRefRepository
import com.maestrx.studentcontrol.teacherapp.viewmodel.di.LessonDetailsViewModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
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
    @Assisted private val lesson: Lesson,
) : ViewModel() {

    private val _lessonState = MutableStateFlow(Lesson())
    val lessonState = _lessonState.asStateFlow()

    private val _controlStatus = MutableStateFlow<ControlStatus>(ControlStatus.NotReadyToStart)
    val controlStatus = _controlStatus.asStateFlow()

    private val _students = MutableStateFlow<List<Attendance>>(mutableListOf())
    val students = _students.asStateFlow()

    init {
        setLesson(lesson)

        attendanceRepository.getByLesson(lesson.id)
            .onEach { list ->
                _students.value = list.map {
                    Attendance.toData(it)
                }
            }
            .launchIn(viewModelScope)
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
            try {
                lessonRepository.deleteById(lesson.id)
            } catch (e: Exception) {
                Log.d("TeacherApp", "Error deleting lesson: $e")
            }
        }
    }

    fun setControlStatus(status: ControlStatus) {
        _controlStatus.update { status }
    }

    fun saveAttendance(studentId: Long) {
        viewModelScope.launch {
            try {
                attendanceRepository.save(
                    AttendanceEntity(
                        lessonId = lesson.id,
                        studentId = studentId,
                    )
                )
            } catch (e: Exception) {
                Log.d("TeacherApp", "Error save attendance: $e")
            }
        }
    }
}