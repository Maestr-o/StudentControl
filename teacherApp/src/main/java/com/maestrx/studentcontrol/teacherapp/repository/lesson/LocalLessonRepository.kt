package com.maestrx.studentcontrol.teacherapp.repository.lesson

import com.maestrx.studentcontrol.teacherapp.db.AppDb
import com.maestrx.studentcontrol.teacherapp.db.entity.LessonEntity
import com.maestrx.studentcontrol.teacherapp.model.LessonResponse
import com.maestrx.studentcontrol.teacherapp.model.StudentResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalLessonRepository @Inject constructor(
    private val db: AppDb,
) : LessonRepository {

    override suspend fun getLessonsForPeriod(startTime: Long, endTime: Long): List<LessonResponse> =
        db.lessonDao.getLessonsForPeriod(startTime, endTime)
            .sortedBy { it.timeStart }

    override suspend fun getStudentsByLessonId(lessonId: Long): List<StudentResponse> =
        db.lessonDao.getStudentsByLessonId(lessonId)
            .sortedWith(compareBy({ it.groupName }, { it.lastName }))

    override suspend fun save(data: LessonEntity): Long =
        db.lessonDao.save(data)

    override suspend fun deleteById(id: Long) =
        db.lessonDao.deleteById(id)

    override suspend fun getLessonsBySubjectAndGroup(
        subjectId: Long,
        groupId: Long
    ): List<LessonResponse> =
        db.lessonDao.getLessonsBySubjectAndGroup(subjectId, groupId)
            .sortedBy { it.timeStart }

    override suspend fun getCountBySubjectAndGroup(subjectId: Long, groupId: Long): Int =
        db.lessonDao.getCountBySubjectAndGroup(subjectId, groupId)

    override fun getCount(): Flow<Long> =
        db.lessonDao.getCount()
}