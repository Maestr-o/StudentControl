package com.maestrx.studentcontrol.teacherapp.repository.mark

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@InstallIn(ViewModelComponent::class)
@Module
interface MarkRepositoryModule {
    @Binds
    fun bindMarkRepository(impl: LocalMarkRepository): MarkRepository
}