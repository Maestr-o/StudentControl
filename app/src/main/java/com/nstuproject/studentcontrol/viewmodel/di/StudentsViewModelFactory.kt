package com.nstuproject.studentcontrol.viewmodel.di

import com.nstuproject.studentcontrol.viewmodel.StudentsViewModel
import dagger.assisted.AssistedFactory

@AssistedFactory
interface StudentsViewModelFactory {
    fun create(groupId: Long): StudentsViewModel
}