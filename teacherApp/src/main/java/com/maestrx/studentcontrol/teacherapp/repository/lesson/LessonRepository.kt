package com.maestrx.studentcontrol.teacherapp.repository.lesson

import com.maestrx.studentcontrol.teacherapp.db.entity.LessonEntity
import com.maestrx.studentcontrol.teacherapp.model.LessonResponse
import kotlinx.coroutines.flow.Flow

interface LessonRepository {
    suspend fun getForPeriod(startTime: Long, endTime: Long): List<LessonResponse>
    suspend fun save(data: LessonEntity): Long
    suspend fun deleteById(id: Long)
    suspend fun getBySubjectIdAndGroupIdAndStartEndTime(
        subjectId: Long,
        groupId: Long,
        timeStart: Long,
        timeEnd: Long,
    ): List<LessonResponse>

    suspend fun getCountBySubjectIdAndGroupIdAndStartEndTime(
        subjectId: Long,
        groupId: Long,
        timeStart: Long,
        timeEnd: Long,
    ): Int

    fun getCount(): Flow<Long>
}