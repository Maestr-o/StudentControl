package com.maestrx.studentcontrol.teacherapp.viewmodel

import com.maestrx.studentcontrol.teacherapp.model.Mark

data class ControlUiState(
    val totalStudentsCount: Int = 0,
    val marks: List<Mark> = mutableListOf(),
    val markedStudentsWithGroups: List<Any> = mutableListOf(),
)