package com.nstuproject.studentcontrol.repository.lesson

import com.nstuproject.studentcontrol.db.entity.LessonEntity
import com.nstuproject.studentcontrol.model.LessonResponse

interface LessonRepository {
    suspend fun getLessonsForPeriod(startTime: Long, endTime: Long): List<LessonResponse>
    suspend fun save(data: LessonEntity): Long
    suspend fun deleteById(id: Long)
}