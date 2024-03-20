package com.nstuproject.studentcontrol.repository.lesson

import com.nstuproject.studentcontrol.db.entity.LessonEntity
import com.nstuproject.studentcontrol.model.LessonResponse
import kotlinx.coroutines.flow.Flow

interface LessonRepository {
    fun getAll(): Flow<List<LessonResponse>>
    suspend fun save(data: LessonEntity)
    suspend fun deleteById(id: Long)
}