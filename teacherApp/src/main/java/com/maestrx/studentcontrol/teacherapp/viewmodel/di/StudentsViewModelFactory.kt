package com.maestrx.studentcontrol.teacherapp.viewmodel.di

import com.maestrx.studentcontrol.teacherapp.viewmodel.StudentsViewModel
import dagger.assisted.AssistedFactory

@AssistedFactory
interface StudentsViewModelFactory {
    fun create(groupId: Long): StudentsViewModel
}