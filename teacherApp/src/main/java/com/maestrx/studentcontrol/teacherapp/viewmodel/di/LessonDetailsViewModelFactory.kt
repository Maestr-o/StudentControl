package com.maestrx.studentcontrol.teacherapp.viewmodel.di

import com.maestrx.studentcontrol.teacherapp.model.Lesson
import com.maestrx.studentcontrol.teacherapp.viewmodel.LessonDetailsViewModel
import dagger.assisted.AssistedFactory

@AssistedFactory
interface LessonDetailsViewModelFactory {
    fun create(lesson: Lesson): LessonDetailsViewModel
}