package com.nstuproject.studentcontrol.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nstuproject.studentcontrol.model.Lesson
import com.nstuproject.studentcontrol.repository.group.GroupRepository
import com.nstuproject.studentcontrol.repository.lesson.LessonRepository
import com.nstuproject.studentcontrol.repository.subject.SubjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LessonsViewModel @Inject constructor(
    private val lessonRepository: LessonRepository,
    private val groupRepository: GroupRepository,
    private val subjectRepository: SubjectRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(emptyList<Lesson>())
    val state = _state.asStateFlow()

    private val _groupsCount = MutableStateFlow(0L)
    val groupsCount = _groupsCount.asStateFlow()

    private val _subjectsCount = MutableStateFlow(0L)
    val subjectsCount = _subjectsCount.asStateFlow()

    init {
        lessonRepository.getAll().onEach { list ->
            _state.update {
                list.map {
                    Lesson.fromResponseToData(it)
                }
            }
        }
            .launchIn(viewModelScope)

        groupRepository.getCount().onEach {
            _groupsCount.value = it
        }
            .launchIn(viewModelScope)

        subjectRepository.getCount().onEach {
            _subjectsCount.value = it
        }
            .launchIn(viewModelScope)
    }
}