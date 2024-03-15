package com.nstuproject.studentcontrol.repository.subject

import com.nstuproject.studentcontrol.db.AppDb
import com.nstuproject.studentcontrol.db.entity.SubjectEntity
import com.nstuproject.studentcontrol.model.Subject
import javax.inject.Inject

class LocalSubjectRepository @Inject constructor(
    private val db: AppDb,
) : SubjectRepository {

    override suspend fun getAll(): List<Subject> =
        db.subjectDao.getAll().map {
            it.toData()
        }

    override suspend fun save(data: Subject) =
        db.subjectDao.save(SubjectEntity.toEntity(data))

    override suspend fun deleteById(id: Long) =
        db.subjectDao.deleteById(id)
}