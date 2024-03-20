package com.nstuproject.studentcontrol.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nstuproject.studentcontrol.model.Group
import com.nstuproject.studentcontrol.model.Lesson
import com.nstuproject.studentcontrol.model.Subject
import com.nstuproject.studentcontrol.repository.group.GroupRepository
import com.nstuproject.studentcontrol.repository.lesson.LessonRepository
import com.nstuproject.studentcontrol.repository.subject.SubjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewLessonViewModel @Inject constructor(
    private val lessonRepository: LessonRepository,
    private val groupRepository: GroupRepository,
    private val subjectRepository: SubjectRepository,
) : ViewModel() {

    private val _lessonState = MutableStateFlow(Lesson())
    val lessonState = _lessonState.asStateFlow()

    private val _subjectsState = MutableStateFlow<List<Subject>>(emptyList())
    val subjectsState = _subjectsState.asStateFlow()

    private val _groupsState = MutableStateFlow<List<Group>>(emptyList())
    val groupsState = _groupsState.asStateFlow()

    init {
        subjectRepository.getAll().onEach { list ->
            _subjectsState.update { _ ->
                list.map {
                    Subject.toData(it)
                }
            }
        }
            .launchIn(viewModelScope)

        groupRepository.getAll().onEach { list ->
            _groupsState.update { _ ->
                list.map {
                    Group.toData(it)
                }
            }
        }
            .launchIn(viewModelScope)
    }

    fun save() {
        viewModelScope.launch(Dispatchers.IO) {
            lessonRepository.save(lessonState.value.toEntity())
        }
    }

    fun updateLessonState(lesson: Lesson) {
        _lessonState.update { lesson }
    }
}