package com.nstuproject.studentcontrol.repository.group

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@InstallIn(ViewModelComponent::class)
@Module
interface GroupRepositoryModule {
    @Binds
    fun bindGroupRepository(impl: LocalGroupRepository): GroupRepository
}