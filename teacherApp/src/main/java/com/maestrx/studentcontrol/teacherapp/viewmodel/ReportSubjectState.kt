package com.maestrx.studentcontrol.teacherapp.viewmodel

import com.maestrx.studentcontrol.teacherapp.model.Subject

data class ReportSubjectState(
    val subjects: List<Subject> = emptyList(),
    val selSubject: Int = 0,
)