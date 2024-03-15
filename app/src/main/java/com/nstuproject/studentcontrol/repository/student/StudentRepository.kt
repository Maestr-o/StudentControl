package com.nstuproject.studentcontrol.repository.student

import com.nstuproject.studentcontrol.db.entity.StudentEntity
import kotlinx.coroutines.flow.Flow

interface StudentRepository {
    fun getAll(): Flow<List<StudentEntity>>
    suspend fun save(data: StudentEntity)
    suspend fun deleteById(id: Long)
}