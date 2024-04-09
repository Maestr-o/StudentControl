package com.maestrx.studentcontrol.teacherapp.repository.lesson

import com.maestrx.studentcontrol.teacherapp.db.entity.LessonEntity
import com.maestrx.studentcontrol.teacherapp.model.LessonResponse

interface LessonRepository {
    suspend fun getLessonsForPeriod(startTime: Long, endTime: Long): List<LessonResponse>
    suspend fun save(data: LessonEntity): Long
    suspend fun deleteById(id: Long)
}