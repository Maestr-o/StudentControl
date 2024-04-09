package com.maestrx.studentcontrol.teacherapp.repository.group

import com.maestrx.studentcontrol.teacherapp.db.AppDb
import com.maestrx.studentcontrol.teacherapp.db.entity.GroupEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalGroupRepository @Inject constructor(
    private val db: AppDb,
) : GroupRepository {

    override fun getAll(): Flow<List<GroupEntity>> =
        db.groupDao.getAll().map { list ->
            list.sortedBy {
                it.name
            }
        }

    override suspend fun save(data: GroupEntity) =
        db.groupDao.save(data)

    override suspend fun deleteById(id: Long) =
        db.groupDao.deleteById(id)

    override fun getCount(): Flow<Long> =
        db.groupDao.getCount()
}