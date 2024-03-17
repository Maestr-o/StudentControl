package com.nstuproject.studentcontrol.viewmodel

import com.nstuproject.studentcontrol.model.Group
import com.nstuproject.studentcontrol.model.Lesson
import com.nstuproject.studentcontrol.model.Subject

data class NewLessonUiState(
    val lesson: Lesson = Lesson(),
    val groups: List<Group> = emptyList(),
    val subjects: List<Subject> = emptyList()
)