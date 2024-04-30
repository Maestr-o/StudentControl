package com.maestrx.studentcontrol.teacherapp.repository.attendance

import com.maestrx.studentcontrol.teacherapp.db.dao.AttendanceDao
import com.maestrx.studentcontrol.teacherapp.db.entity.AttendanceEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalAttendanceRepository @Inject constructor(
    private val attendanceDao: AttendanceDao,
) : AttendanceRepository {
    override fun getByLessonId(lessonId: Long): Flow<List<AttendanceEntity>> =
        attendanceDao.getByLessonId(lessonId)

    override suspend fun save(data: AttendanceEntity) =
        attendanceDao.save(data)

    override suspend fun getBySubjectIdAndGroupId(
        subjectId: Long,
        groupId: Long
    ): List<AttendanceEntity> =
        attendanceDao.getBySubjectIdAndGroupId(subjectId, groupId)

    override suspend fun getCountByLessonIdAndGroupId(lessonId: Long, groupId: Long): Int =
        attendanceDao.getCountByLessonIdAndGroupId(lessonId, groupId)

    override suspend fun getCount(): Int =
        attendanceDao.getCount()

    override suspend fun saveList(list: List<AttendanceEntity>) =
        attendanceDao.saveList(list)
}