package com.nstuproject.studentcontrol.repository.lesson

import com.nstuproject.studentcontrol.db.entity.LessonEntity
import kotlinx.coroutines.flow.Flow

interface LessonRepository {
    fun getAll(): Flow<List<LessonEntity>>
    suspend fun save(data: LessonEntity)
    suspend fun deleteById(id: Long)
}