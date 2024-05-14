package com.maestrx.studentcontrol.teacherapp.repository.subject

import com.maestrx.studentcontrol.teacherapp.db.entity.SubjectEntity
import kotlinx.coroutines.flow.Flow

interface SubjectRepository {
    fun getAll(): Flow<List<SubjectEntity>>
    suspend fun save(data: SubjectEntity)
    suspend fun deleteById(id: Long)
    fun getCount(): Flow<Long>
    suspend fun getByGroupId(groupId: Long): List<SubjectEntity>
}