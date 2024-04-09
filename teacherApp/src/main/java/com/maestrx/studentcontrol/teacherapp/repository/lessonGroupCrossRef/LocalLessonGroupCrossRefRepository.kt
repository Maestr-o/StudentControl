package com.maestrx.studentcontrol.teacherapp.repository.lessonGroupCrossRef

import com.maestrx.studentcontrol.teacherapp.db.AppDb
import com.maestrx.studentcontrol.teacherapp.db.entity.GroupEntity
import com.maestrx.studentcontrol.teacherapp.model.LessonGroupCrossRef
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalLessonGroupCrossRefRepository @Inject constructor(
    private val db: AppDb,
) : LessonGroupCrossRefRepository {

    override fun getGroupsByLesson(lessonId: Long): Flow<List<GroupEntity>> =
        db.lessonGroupCrossRefDao.getGroupsByLesson(lessonId)

    override suspend fun save(data: List<LessonGroupCrossRef>) {
        data.map {
            db.lessonGroupCrossRefDao.save(it.toEntity())
        }
    }

    override suspend fun clear(lessonId: Long) = db.lessonGroupCrossRefDao.clear(lessonId)
}