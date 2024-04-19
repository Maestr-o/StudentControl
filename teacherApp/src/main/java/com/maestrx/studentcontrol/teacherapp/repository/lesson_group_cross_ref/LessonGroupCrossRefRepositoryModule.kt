package com.maestrx.studentcontrol.teacherapp.repository.lesson_group_cross_ref

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@InstallIn(ViewModelComponent::class)
@Module
interface LessonGroupCrossRefRepositoryModule {
    @Binds
    fun bindLessonRepository(impl: LocalLessonGroupCrossRefRepository): LessonGroupCrossRefRepository
}