package com.nstuproject.studentcontrol.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nstuproject.studentcontrol.model.Group
import com.nstuproject.studentcontrol.model.Lesson
import com.nstuproject.studentcontrol.repository.lessonGroupCrossRef.LessonGroupCrossRefRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LessonDetailsViewModel @Inject constructor(
    private val lessonGroupCrossRefRepository: LessonGroupCrossRefRepository,
) : ViewModel() {

    private val _lessonState = MutableStateFlow(Lesson())
    val lessonState = _lessonState.asStateFlow()

    fun setLesson(lesson: Lesson) {
        lessonGroupCrossRefRepository.getGroupsByLesson(lesson.id)
            .onEach { groups ->
                val lessonWithGroups =
                    lesson.copy(
                        groups = groups.map {
                            Group.toData(it)
                        }
                    )
                _lessonState.update { lessonWithGroups }
            }
            .launchIn(viewModelScope)
    }
}