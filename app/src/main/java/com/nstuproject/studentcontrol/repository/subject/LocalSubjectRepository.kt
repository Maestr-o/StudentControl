package com.nstuproject.studentcontrol.repository.subject

import com.nstuproject.studentcontrol.db.AppDb
import com.nstuproject.studentcontrol.db.entity.SubjectEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalSubjectRepository @Inject constructor(
    private val db: AppDb,
) : SubjectRepository {

    override fun getAll(): Flow<List<SubjectEntity>> =
        db.subjectDao.getAll()

    override suspend fun save(data: SubjectEntity) =
        db.subjectDao.save(data)

    override suspend fun deleteById(id: Long) =
        db.subjectDao.deleteById(id)
}