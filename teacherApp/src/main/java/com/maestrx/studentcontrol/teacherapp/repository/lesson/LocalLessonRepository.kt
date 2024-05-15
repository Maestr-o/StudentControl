package com.maestrx.studentcontrol.teacherapp.repository.lesson

import com.maestrx.studentcontrol.teacherapp.db.dao.LessonDao
import com.maestrx.studentcontrol.teacherapp.db.entity.LessonEntity
import com.maestrx.studentcontrol.teacherapp.model.LessonResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalLessonRepository @Inject constructor(
    private val lessonDao: LessonDao,
) : LessonRepository {

    override suspend fun getForPeriod(startTime: Long, endTime: Long): List<LessonResponse> =
        lessonDao.getForPeriod(startTime, endTime)
            .sortedBy { it.timeStart }

    override suspend fun save(data: LessonEntity): Long =
        lessonDao.save(data)

    override suspend fun deleteById(id: Long) =
        lessonDao.deleteById(id)

    override suspend fun getBySubjectIdAndGroupIdAndStartTime(
        subjectId: Long,
        groupId: Long,
        timeStart: Long
    ): List<LessonResponse> =
        lessonDao.getBySubjectIdAndGroupIdAndStartTime(subjectId, groupId, timeStart)
            .sortedBy { it.timeStart }

    override suspend fun getCountBySubjectIdAndGroupIdAndStartTime(
        subjectId: Long,
        groupId: Long,
        timeStart: Long
    ): Int =
        lessonDao.getCountBySubjectIdAndGroupIdAndStartTime(subjectId, groupId, timeStart)

    override fun getCount(): Flow<Long> =
        lessonDao.getCount()
}