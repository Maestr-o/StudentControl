package com.maestrx.studentcontrol.teacherapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maestrx.studentcontrol.teacherapp.model.Group
import com.maestrx.studentcontrol.teacherapp.model.Lesson
import com.maestrx.studentcontrol.teacherapp.repository.group.GroupRepository
import com.maestrx.studentcontrol.teacherapp.repository.lesson.LessonRepository
import com.maestrx.studentcontrol.teacherapp.repository.subject.SubjectRepository
import com.maestrx.studentcontrol.teacherapp.util.Constants
import com.maestrx.studentcontrol.teacherapp.util.TimeFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LessonsViewModel @Inject constructor(
    private val lessonRepository: LessonRepository,
    private val groupRepository: GroupRepository,
    subjectRepository: SubjectRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(emptyList<Lesson>())
    val state = _state.asStateFlow()

    private val _date = MutableStateFlow(TimeFormatter.getCurrentDateZeroTime())
    val date = _date.asStateFlow()

    private val _groupsCount = MutableStateFlow(0L)
    val groupsCount = _groupsCount.asStateFlow()

    private val _subjectsCount = MutableStateFlow(0L)
    val subjectsCount = _subjectsCount.asStateFlow()

    private val _lessonsCount = MutableStateFlow(0L)
    val lessonsCount = _lessonsCount.asStateFlow()

    init {
        date
            .onEach {
                updateLessonsForPeriod(_date.value, TimeFormatter.getEndTime(_date.value))
            }
            .launchIn(viewModelScope)

        groupRepository.getCount()
            .onEach {
                _groupsCount.value = it
            }
            .launchIn(viewModelScope)

        subjectRepository.getCount()
            .onEach {
                _subjectsCount.value = it
            }
            .launchIn(viewModelScope)

        lessonRepository.getCount()
            .onEach {
                _lessonsCount.value = it
            }
            .launchIn(viewModelScope)
    }

    fun updateLessonsForPeriod(startTime: Long, endTime: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _state.update {
                    lessonRepository.getForPeriod(startTime, endTime).map { lessonRes ->
                        Lesson.fromResponseToData(lessonRes).copy(
                            groups = groupRepository.getByLessonId(lessonRes.id).first()
                                .map { groupEnt ->
                                    Group.toData(groupEnt)
                                }
                        )
                    }
                }
            } catch (e: Exception) {
                Log.d(
                    Constants.DEBUG_TAG,
                    "Error updating lessons for period: ${e.printStackTrace()}"
                )
            }
        }
    }

    fun setDate(time: Long) {
        _date.update { time }
    }

    fun incDate() {
        _date.update {
            TimeFormatter.incDay(it)
        }
    }

    fun decDate() {
        _date.update {
            TimeFormatter.decDay(it)
        }
    }
}