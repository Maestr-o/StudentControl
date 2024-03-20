package com.nstuproject.studentcontrol.repository.group

import com.nstuproject.studentcontrol.db.entity.GroupEntity
import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    fun getAll(): Flow<List<GroupEntity>>
    suspend fun save(data: GroupEntity)
    suspend fun deleteById(id: Long)
    fun getCount(): Flow<Long>
}