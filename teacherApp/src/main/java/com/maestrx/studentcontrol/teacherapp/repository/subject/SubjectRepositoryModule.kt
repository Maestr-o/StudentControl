package com.maestrx.studentcontrol.teacherapp.repository.subject

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@InstallIn(ViewModelComponent::class)
@Module
interface SubjectRepositoryModule {
    @Binds
    fun bindSubjectRepository(impl: LocalSubjectRepository): SubjectRepository
}