package com.nstuproject.studentcontrol.repository.lesson

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