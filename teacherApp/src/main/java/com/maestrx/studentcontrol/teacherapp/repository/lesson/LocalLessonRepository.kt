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

    override suspend fun getBySubjectIdAndGroupIdAndStartEndTime(
        subjectId: Long,
        groupId: Long,
        timeStart: Long,
        timeEnd: Long,
    ): List<LessonResponse> =
        lessonDao.getBySubjectIdAndGroupIdAndStartEndTime(subjectId, groupId, timeStart, timeEnd)
            .sortedBy { it.timeStart }

    override suspend fun getCountBySubjectIdAndGroupIdAndStartEndTime(
        subjectId: Long,
        groupId: Long,
        timeStart: Long,
        timeEnd: Long,
    ): Int =
        lessonDao.getCountBySubjectIdAndGroupIdAndStartEndTime(
            subjectId,
            groupId,
            timeStart,
            timeEnd
        )

    override fun getCount(): Flow<Long> =
        lessonDao.getCount()
}