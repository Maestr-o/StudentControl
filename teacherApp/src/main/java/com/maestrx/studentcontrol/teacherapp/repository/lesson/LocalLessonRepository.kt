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

    override suspend fun getBySubjectIdAndGroupId(
        subjectId: Long,
        groupId: Long
    ): List<LessonResponse> =
        lessonDao.getBySubjectIdAndGroupId(subjectId, groupId)
            .sortedBy { it.timeStart }

    override suspend fun getCountBySubjectIdAndGroupId(subjectId: Long, groupId: Long): Int =
        lessonDao.getCountBySubjectIdAndGroupId(subjectId, groupId)

    override fun getCount(): Flow<Long> =
        lessonDao.getCount()
}