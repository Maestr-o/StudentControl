package com.maestrx.studentcontrol.teacherapp.viewmodel.di

import com.maestrx.studentcontrol.teacherapp.viewmodel.NewLessonViewModel
import dagger.assisted.AssistedFactory

@AssistedFactory
interface NewLessonViewModelFactory {
    fun create(selDate: Long): NewLessonViewModel
}