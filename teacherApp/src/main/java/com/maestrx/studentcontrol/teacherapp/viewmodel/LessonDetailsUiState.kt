package com.maestrx.studentcontrol.teacherapp.viewmodel

import com.maestrx.studentcontrol.teacherapp.model.Attendance

data class LessonDetailsUiState(
    val totalStudentsCount: Int = 0,
    val attendance: List<Attendance> = mutableListOf(),
    val markedStudentsWithGroups: List<Any> = mutableListOf(),
    val notMarkedStudentsWithGroups: List<Any> = mutableListOf(),
)