package com.maestrx.studentcontrol.teacherapp.viewmodel.di

import com.maestrx.studentcontrol.teacherapp.model.Student
import com.maestrx.studentcontrol.teacherapp.viewmodel.ReportViewModel
import dagger.assisted.AssistedFactory

@AssistedFactory
interface ReportViewModelFactory {
    fun create(student: Student): ReportViewModel
}