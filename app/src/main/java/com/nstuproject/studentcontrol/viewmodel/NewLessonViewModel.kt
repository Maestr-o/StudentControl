package com.nstuproject.studentcontrol.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nstuproject.studentcontrol.db.entity.LessonEntity
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewLessonViewModel @Inject constructor(
    private val lessonRepository: LessonRepository,
    private val groupRepository: GroupRepository,
    private val subjectRepository: SubjectRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(NewLessonUiState())
    val state = _state.asStateFlow()

    init {
        subjectRepository.getAll().onEach { list ->
            _state.update { state ->
                state.copy(
                    subjects = list.map {
                        it.toData()
                    }
                )
            }
        }
            .launchIn(viewModelScope)

        groupRepository.getAll().onEach { list ->
            _state.update { state ->
                state.copy(
                    groups = list.map {
                        it.toData()
                    }
                )
            }
        }
            .launchIn(viewModelScope)
    }

    fun save(lesson: Lesson) {
        viewModelScope.launch {
            try {
                lessonRepository.save(LessonEntity.toEntity(lesson))
            } catch (e: Exception) {
                Log.e("TEST", e.toString())
            }
        }
    }
}