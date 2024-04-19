package com.maestrx.studentcontrol.teacherapp.viewmodel

import com.maestrx.studentcontrol.teacherapp.model.Attendance

data class LessonDetailsUiState(
    val attendance: List<Attendance> = mutableListOf(),
    val studentsWithGroups: List<Any> = mutableListOf(),
)