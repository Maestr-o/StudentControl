package com.nstuproject.studentcontrol.repository.subject

import com.nstuproject.studentcontrol.db.entity.SubjectEntity
import kotlinx.coroutines.flow.Flow

interface SubjectRepository {
    fun getAll(): Flow<List<SubjectEntity>>
    suspend fun save(data: SubjectEntity)
    suspend fun deleteById(id: Long)
}