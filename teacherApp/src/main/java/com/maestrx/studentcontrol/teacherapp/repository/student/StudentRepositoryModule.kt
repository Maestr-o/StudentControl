package com.maestrx.studentcontrol.teacherapp.repository.student

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@InstallIn(ViewModelComponent::class)
@Module
interface StudentRepositoryModule {
    @Binds
    fun bindStudentRepository(impl: LocalStudentRepository): StudentRepository
}