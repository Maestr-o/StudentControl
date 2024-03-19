package com.nstuproject.studentcontrol.repository.lesson

import com.nstuproject.studentcontrol.db.entity.LessonEntity
import com.nstuproject.studentcontrol.model.Lesson
import kotlinx.coroutines.flow.Flow

interface LessonRepository {
    fun getAll(): Flow<List<Lesson>>
    suspend fun save(data: LessonEntity)
    suspend fun deleteById(id: Long)
}