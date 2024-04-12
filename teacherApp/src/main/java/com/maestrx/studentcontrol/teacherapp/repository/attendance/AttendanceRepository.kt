package com.maestrx.studentcontrol.teacherapp.repository.attendance

import com.maestrx.studentcontrol.teacherapp.db.entity.AttendanceEntity
import kotlinx.coroutines.flow.Flow

interface AttendanceRepository {
    fun getByLesson(lessonId: Long): Flow<List<AttendanceEntity>>
    suspend fun save(data: AttendanceEntity)
}