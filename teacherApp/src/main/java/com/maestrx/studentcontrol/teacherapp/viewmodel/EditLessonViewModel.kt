package com.maestrx.studentcontrol.teacherapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maestrx.studentcontrol.teacherapp.model.Group
import com.maestrx.studentcontrol.teacherapp.model.Lesson
import com.maestrx.studentcontrol.teacherapp.model.LessonGroupCross
import com.maestrx.studentcontrol.teacherapp.model.Subject
import com.maestrx.studentcontrol.teacherapp.repository.group.GroupRepository
import com.maestrx.studentcontrol.teacherapp.repository.lesson.LessonRepository
import com.maestrx.studentcontrol.teacherapp.repository.lesson_group_cross.LessonGroupCrossRepository
import com.maestrx.studentcontrol.teacherapp.repository.subject.SubjectRepository
import com.maestrx.studentcontrol.teacherapp.utils.Constants
import com.maestrx.studentcontrol.teacherapp.utils.Event
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
    private val lessonGroupCrossRepository: LessonGroupCrossRepository,
) : ViewModel() {

    private val _lessonState = MutableStateFlow(Lesson())
    val lessonState = _lessonState.asStateFlow()

    private val _subjectsState = MutableStateFlow<List<Subject>>(emptyList())
    val subjectsState = _subjectsState.asStateFlow()

    private val _groupsState = MutableStateFlow<List<Group>>(emptyList())
    val groupsState = _groupsState.asStateFlow()

    private val _selectedGroupsState = MutableStateFlow(GroupsChooseUiState())
    val selectedGroupsState = _selectedGroupsState.asStateFlow()

    private val _message = MutableStateFlow(Event(""))
    val message = _message.asStateFlow()

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
            updateGroups(lessonState.value.groups)
        }
            .launchIn(viewModelScope)
    }

    fun save() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val lessonId = lessonState.value.id
                val lessonGroupCrosses: MutableList<LessonGroupCross> = mutableListOf()
                lessonGroupCrosses += selectedGroupsState.value.selectedGroups.map {
                    LessonGroupCross(
                        lessonId = lessonId,
                        groupId = it.id,
                    )
                }
                lessonRepository.save(lessonState.value.toEntity())
                lessonGroupCrossRepository.clear(lessonId)
                lessonGroupCrossRepository.save(lessonGroupCrosses)
            } catch (e: Exception) {
                _message.value = Event(Constants.MESSAGE_ERROR_SAVING_LESSON)
                Log.d(Constants.DEBUG_TAG, "Error saving lesson: $e")
            }
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
        viewModelScope.launch(Dispatchers.Default) {
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
    }

    fun deleteLesson() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                lessonRepository.deleteById(lessonState.value.id)
            } catch (e: Exception) {
                _message.value = Event(Constants.MESSAGE_ERROR_DELETING_LESSON)
                Log.d(Constants.DEBUG_TAG, "Error deleting lesson: $e")
            }
        }
    }
}