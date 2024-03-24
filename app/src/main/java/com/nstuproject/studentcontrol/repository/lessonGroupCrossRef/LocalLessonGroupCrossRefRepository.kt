package com.nstuproject.studentcontrol.repository.lessonGroupCrossRef

import com.nstuproject.studentcontrol.db.AppDb
import com.nstuproject.studentcontrol.db.entity.GroupEntity
import com.nstuproject.studentcontrol.model.LessonGroupCrossRef
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
}