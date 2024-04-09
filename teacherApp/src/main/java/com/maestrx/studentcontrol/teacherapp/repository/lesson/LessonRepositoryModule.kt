package com.maestrx.studentcontrol.teacherapp.repository.lesson

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@InstallIn(ViewModelComponent::class)
@Module
interface LessonRepositoryModule {
    @Binds
    fun bindLessonRepository(impl: LocalLessonRepository): LessonRepository
}