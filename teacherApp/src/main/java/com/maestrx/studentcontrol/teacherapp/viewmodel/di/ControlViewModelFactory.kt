package com.maestrx.studentcontrol.teacherapp.viewmodel.di

import com.maestrx.studentcontrol.teacherapp.model.Lesson
import com.maestrx.studentcontrol.teacherapp.viewmodel.ControlViewModel
import dagger.assisted.AssistedFactory

@AssistedFactory
interface ControlViewModelFactory {
    fun create(lesson: Lesson): ControlViewModel
}