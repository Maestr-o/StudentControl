package com.maestrx.studentcontrol.teacherapp.repository.subject

import com.maestrx.studentcontrol.teacherapp.db.dao.SubjectDao
import com.maestrx.studentcontrol.teacherapp.db.entity.SubjectEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalSubjectRepository @Inject constructor(
    private val subjectDao: SubjectDao,
) : SubjectRepository {

    override fun getAll(): Flow<List<SubjectEntity>> =
        subjectDao.getAll().map { list ->
            list.sortedBy {
                it.name
            }
        }

    override suspend fun save(data: SubjectEntity) =
        subjectDao.save(data)

    override suspend fun deleteById(id: Long) =
        subjectDao.deleteById(id)

    override fun getCount(): Flow<Long> =
        subjectDao.getCount()
}