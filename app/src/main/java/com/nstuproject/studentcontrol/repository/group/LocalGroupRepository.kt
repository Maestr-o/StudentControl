package com.nstuproject.studentcontrol.repository.group

import com.nstuproject.studentcontrol.db.AppDb
import com.nstuproject.studentcontrol.db.entity.GroupEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalGroupRepository @Inject constructor(
    private val db: AppDb,
) : GroupRepository {

    override fun getAll(): Flow<List<GroupEntity>> =
        db.groupDao.getAll()

    override suspend fun save(data: GroupEntity) =
        db.groupDao.save(data)

    override suspend fun deleteById(id: Long) =
        db.groupDao.deleteById(id)
}