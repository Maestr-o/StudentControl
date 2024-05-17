package com.maestrx.studentcontrol.teacherapp.model

data class ReportLesson(
    val lesson: Lesson = Lesson(),
    var isMarked: Boolean = false,
)