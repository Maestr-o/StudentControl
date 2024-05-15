package com.maestrx.studentcontrol.teacherapp.viewmodel

import com.maestrx.studentcontrol.teacherapp.model.Mark
import com.maestrx.studentcontrol.teacherapp.model.ReportLesson
import com.maestrx.studentcontrol.teacherapp.model.Student
import com.maestrx.studentcontrol.teacherapp.model.Subject

data class ReportUiState(
    val subject: Subject = Subject(),
    val student: Student = Student(),
    val reportLessons: List<ReportLesson> = emptyList(),
    val marks: List<Mark> = emptyList(),
    val percentage: Float = 0f,
)