package com.nstuproject.studentcontrol.viewmodel.di

import com.nstuproject.studentcontrol.model.Lesson
import com.nstuproject.studentcontrol.viewmodel.LessonDetailsViewModel
import dagger.assisted.AssistedFactory

@AssistedFactory
interface LessonDetailsViewModelFactory {
    fun create(lesson: Lesson): LessonDetailsViewModel
}