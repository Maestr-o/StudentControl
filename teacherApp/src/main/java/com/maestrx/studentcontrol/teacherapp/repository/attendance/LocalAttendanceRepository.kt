package com.maestrx.studentcontrol.teacherapp.repository.attendance

import com.maestrx.studentcontrol.teacherapp.db.AppDb
import com.maestrx.studentcontrol.teacherapp.db.entity.AttendanceEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalAttendanceRepository @Inject constructor(
    private val db: AppDb,
) : AttendanceRepository {
    override fun getByLesson(lessonId: Long): Flow<List<AttendanceEntity>> =
        db.attendanceDao.getByLesson(lessonId)

    override suspend fun save(data: AttendanceEntity) =
        db.attendanceDao.save(data)

    override suspend fun getBySubjectAndGroup(
        subjectId: Long,
        groupId: Long
    ): List<AttendanceEntity> =
        db.attendanceDao.getBySubjectAndGroup(subjectId, groupId)
}