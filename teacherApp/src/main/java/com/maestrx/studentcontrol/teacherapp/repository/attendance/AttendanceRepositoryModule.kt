package com.maestrx.studentcontrol.teacherapp.repository.attendance

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@InstallIn(ViewModelComponent::class)
@Module
interface AttendanceRepositoryModule {
    @Binds
    fun bindAttendanceRepository(impl: LocalAttendanceRepository): AttendanceRepository
}