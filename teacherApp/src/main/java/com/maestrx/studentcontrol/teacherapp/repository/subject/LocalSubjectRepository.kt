package com.maestrx.studentcontrol.teacherapp.repository.subject

import com.maestrx.studentcontrol.teacherapp.db.AppDb
import com.maestrx.studentcontrol.teacherapp.db.entity.SubjectEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalSubjectRepository @Inject constructor(
    private val db: AppDb,
) : SubjectRepository {

    override fun getAll(): Flow<List<SubjectEntity>> =
        db.subjectDao.getAll().map { list ->
            list.sortedBy {
                it.name
            }
        }

    override suspend fun save(data: SubjectEntity) =
        db.subjectDao.save(data)

    override suspend fun deleteById(id: Long) =
        db.subjectDao.deleteById(id)

    override fun getCount(): Flow<Long> =
        db.subjectDao.getCount()
}