package com.maestrx.studentcontrol.teacherapp.viewmodel

import com.maestrx.studentcontrol.teacherapp.model.Lesson
import com.maestrx.studentcontrol.teacherapp.model.Mark
import com.maestrx.studentcontrol.teacherapp.model.Student

data class ReportUiState(
    val student: Student = Student(),
    val lessons: List<Lesson> = emptyList(),
    val marks: List<Mark> = emptyList(),
    val percentage: Float = 0f,
)