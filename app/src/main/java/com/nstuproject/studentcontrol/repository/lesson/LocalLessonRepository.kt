package com.nstuproject.studentcontrol.repository.lesson

import com.nstuproject.studentcontrol.db.AppDb
import com.nstuproject.studentcontrol.db.entity.LessonEntity
import com.nstuproject.studentcontrol.model.LessonResponse
import javax.inject.Inject

class LocalLessonRepository @Inject constructor(
    private val db: AppDb,
) : LessonRepository {

    override suspend fun getLessonsForPeriod(startTime: Long, endTime: Long): List<LessonResponse> =
        db.lessonDao.getLessonsForPeriod(startTime, endTime)
            .sortedBy { it.timeStart }

    override suspend fun save(data: LessonEntity) =
        db.lessonDao.save(data)

    override suspend fun deleteById(id: Long) =
        db.lessonDao.deleteById(id)
}