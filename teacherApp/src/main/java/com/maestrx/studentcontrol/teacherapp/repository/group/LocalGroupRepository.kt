package com.maestrx.studentcontrol.teacherapp.repository.group

import com.maestrx.studentcontrol.teacherapp.db.dao.GroupDao
import com.maestrx.studentcontrol.teacherapp.db.entity.GroupEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalGroupRepository @Inject constructor(
    private val groupDao: GroupDao,
) : GroupRepository {

    override fun getAll(): Flow<List<GroupEntity>> =
        groupDao.getAll().map { list ->
            list.sortedBy {
                it.name
            }
        }

    override suspend fun save(data: GroupEntity) =
        groupDao.save(data)

    override suspend fun deleteById(id: Long) =
        groupDao.deleteById(id)

    override fun getCount(): Flow<Long> =
        groupDao.getCount()

    override fun getByLessonId(lessonId: Long): Flow<List<GroupEntity>> =
        groupDao.getByLessonId(lessonId)
}