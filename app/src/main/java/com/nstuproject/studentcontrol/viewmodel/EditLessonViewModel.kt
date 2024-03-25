package com.nstuproject.studentcontrol.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nstuproject.studentcontrol.model.Group
import com.nstuproject.studentcontrol.model.Lesson
import com.nstuproject.studentcontrol.model.LessonGroupCrossRef
import com.nstuproject.studentcontrol.model.Subject
import com.nstuproject.studentcontrol.repository.group.GroupRepository
import com.nstuproject.studentcontrol.repository.lesson.LessonRepository
import com.nstuproject.studentcontrol.repository.lessonGroupCrossRef.LessonGroupCrossRefRepository
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
class EditLessonViewModel @Inject constructor(
    private val lessonRepository: LessonRepository,
    groupRepository: GroupRepository,
    subjectRepository: SubjectRepository,
    private val lessonGroupCrossRefRepository: LessonGroupCrossRefRepository,
) : ViewModel() {

    private val _lessonState = MutableStateFlow(Lesson())
    val lessonState = _lessonState.asStateFlow()

    private val _subjectsState = MutableStateFlow<List<Subject>>(emptyList())
    val subjectsState = _subjectsState.asStateFlow()

    private val _groupsState = MutableStateFlow<List<Group>>(emptyList())
    val groupsState = _groupsState.asStateFlow()

    private val _selectedGroupsState = MutableStateFlow(GroupsChooseUiState())
    val selectedGroupsState = _selectedGroupsState.asStateFlow()

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
            val lessonId = lessonState.value.id
            val lessonGroupCrossRefs: MutableList<LessonGroupCrossRef> = mutableListOf()
            lessonGroupCrossRefs += selectedGroupsState.value.selectedGroups.map {
                LessonGroupCrossRef(
                    lessonId = lessonId,
                    groupId = it.id,
                )
            }
            lessonRepository.save(lessonState.value.toEntity())
            lessonGroupCrossRefRepository.clear(lessonId)
            lessonGroupCrossRefRepository.save(lessonGroupCrossRefs)
        }
    }

    fun updateLessonState(lesson: Lesson) {
        _lessonState.update { lesson }
    }

    fun removeGroup(item: Group, id: Int) {
        _selectedGroupsState.update { state ->
            val selGroups = state.selectedGroups - item
            val selPos = state.selectedPositions - id
            GroupsChooseUiState(selPos, selGroups)
        }
    }

    fun addGroup(item: Group, id: Int) {
        _selectedGroupsState.update { state ->
            val selGroups = state.selectedGroups + item
            val selPos = state.selectedPositions + id
            GroupsChooseUiState(selPos, selGroups)
        }
    }

    fun updateGroups(selGroups: List<Group>) {
        val groups = groupsState.value.sortedBy { it.name }

        val positions = mutableListOf<Int>()
        groups.forEachIndexed { index, group ->
            if (selGroups.contains(group)) {
                positions += index
            }
        }

        _selectedGroupsState.update { _ ->
            GroupsChooseUiState(positions, selGroups)
        }
    }

    fun deleteLesson() {
        viewModelScope.launch {
            lessonRepository.deleteById(lessonState.value.id)
        }
    }
}