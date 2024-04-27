package com.maestrx.studentcontrol.teacherapp.repository.lesson_group_cross

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@InstallIn(ViewModelComponent::class)
@Module
interface LessonGroupCrossRepositoryModule {
    @Binds
    fun bindLessonRepository(impl: LocalLessonGroupCrossRepository): LessonGroupCrossRepository
}