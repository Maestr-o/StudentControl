package com.maestrx.studentcontrol.teacherapp.repository.attendance

import com.maestrx.studentcontrol.teacherapp.db.entity.AttendanceEntity
import kotlinx.coroutines.flow.Flow

interface AttendanceRepository {
    fun getByLessonId(lessonId: Long): Flow<List<AttendanceEntity>>
    suspend fun save(data: AttendanceEntity)
    suspend fun getBySubjectIdAndGroupId(subjectId: Long, groupId: Long): List<AttendanceEntity>
    suspend fun getCountByLessonIdAndGroupId(lessonId: Long, groupId: Long): Int
    suspend fun getCount(): Int
}