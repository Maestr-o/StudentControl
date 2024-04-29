package com.maestrx.studentcontrol.teacherapp.model

data class StudentMark(
    val id: Long = 0L,
    val fullName: String = "",
    var isAttended: Boolean = false,
)