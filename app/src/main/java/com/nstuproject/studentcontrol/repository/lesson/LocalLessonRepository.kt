package com.nstuproject.studentcontrol.repository.lesson

import com.nstuproject.studentcontrol.db.AppDb
import com.nstuproject.studentcontrol.db.entity.LessonEntity
import com.nstuproject.studentcontrol.model.Lesson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalLessonRepository @Inject constructor(
    private val db: AppDb,
) : LessonRepository {

    override fun getAll(): Flow<List<Lesson>> =
        db.lessonDao.getLessons().map { list ->
            list.sortedBy {
                it.time
            }
        }.map { list ->
            list.map {
                it.toData()
            }
        }

    override suspend fun save(data: LessonEntity) =
        db.lessonDao.save(data)

    override suspend fun deleteById(id: Long) =
        db.lessonDao.deleteById(id)
}