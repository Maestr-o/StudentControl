package com.maestrx.studentcontrol.teacherapp.repository.lesson_group_cross

import com.maestrx.studentcontrol.teacherapp.db.dao.LessonGroupCrossDao
import com.maestrx.studentcontrol.teacherapp.model.LessonGroupCross
import javax.inject.Inject

class LocalLessonGroupCrossRepository @Inject constructor(
    private val lessonGroupCrossDao: LessonGroupCrossDao,
) : LessonGroupCrossRepository {

    override suspend fun save(data: List<LessonGroupCross>) {
        data.map {
            lessonGroupCrossDao.save(it.toEntity())
        }
    }

    override suspend fun clear(lessonId: Long) =
        lessonGroupCrossDao.clear(lessonId)
}