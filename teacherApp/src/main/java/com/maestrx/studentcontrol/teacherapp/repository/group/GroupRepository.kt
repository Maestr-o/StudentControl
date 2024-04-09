package com.maestrx.studentcontrol.teacherapp.repository.group

import com.maestrx.studentcontrol.teacherapp.db.entity.GroupEntity
import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    fun getAll(): Flow<List<GroupEntity>>
    suspend fun save(data: GroupEntity)
    suspend fun deleteById(id: Long)
    fun getCount(): Flow<Long>
}