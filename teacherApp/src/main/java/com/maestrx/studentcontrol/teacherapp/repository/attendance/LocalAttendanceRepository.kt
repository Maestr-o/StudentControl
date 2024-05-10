package com.maestrx.studentcontrol.teacherapp.repository.attendance

import com.maestrx.studentcontrol.teacherapp.db.dao.MarkDao
import com.maestrx.studentcontrol.teacherapp.db.entity.MarkEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalAttendanceRepository @Inject constructor(
    private val markDao: MarkDao,
) : AttendanceRepository {
    override fun getByLessonId(lessonId: Long): Flow<List<MarkEntity>> =
        markDao.getByLessonId(lessonId)

    override suspend fun save(data: MarkEntity) =
        markDao.save(data)

    override suspend fun getBySubjectIdAndGroupId(
        subjectId: Long,
        groupId: Long
    ): List<MarkEntity> =
        markDao.getBySubjectIdAndGroupId(subjectId, groupId)

    override suspend fun getCountByLessonIdAndGroupId(lessonId: Long, groupId: Long): Int =
        markDao.getCountByLessonIdAndGroupId(lessonId, groupId)

    override suspend fun getCount(): Int =
        markDao.getCount()

    override suspend fun saveList(list: List<MarkEntity>) =
        markDao.saveList(list)
}