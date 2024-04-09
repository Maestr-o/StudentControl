package com.maestrx.studentcontrol.teacherapp.repository.lesson

import com.maestrx.studentcontrol.teacherapp.db.AppDb
import com.maestrx.studentcontrol.teacherapp.db.entity.LessonEntity
import com.maestrx.studentcontrol.teacherapp.model.LessonResponse
import javax.inject.Inject

class LocalLessonRepository @Inject constructor(
    private val db: AppDb,
) : LessonRepository {

    override suspend fun getLessonsForPeriod(startTime: Long, endTime: Long): List<LessonResponse> =
        db.lessonDao.getLessonsForPeriod(startTime, endTime)
            .sortedBy { it.timeStart }

    override suspend fun save(data: LessonEntity): Long =
        db.lessonDao.save(data)

    override suspend fun deleteById(id: Long) =
        db.lessonDao.deleteById(id)
}