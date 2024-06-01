package com.maestrx.studentcontrol.teacherapp.repository.mark

import com.maestrx.studentcontrol.teacherapp.db.entity.MarkEntity
import kotlinx.coroutines.flow.Flow

interface MarkRepository {
    fun getCountByLessonId(lessonId: Long): Flow<Int>
    suspend fun getByLessonIdAndGroupId(lessonId: Long, groupId: Long): List<MarkEntity>
    suspend fun save(data: MarkEntity)
    suspend fun saveList(list: List<MarkEntity>)
    suspend fun getBySubjectIdAndGroupIdAndStartTime(
        subjectId: Long,
        groupId: Long,
        timeStart: Long
    ): List<MarkEntity>

    suspend fun getCountByLessonIdAndGroupId(lessonId: Long, groupId: Long): Int
    suspend fun getCount(): Int
    suspend fun getByStudentIdAndSubjectIdAndStartTime(
        studentId: Long,
        subjectId: Long,
        startTime: Long,
    ): List<MarkEntity>

    suspend fun delete(mark: MarkEntity)
}