package com.maestrx.studentcontrol.teacherapp.model

data class ReportLesson(
    val lesson: Lesson = Lesson(),
    val isMarked: Boolean = false,
)