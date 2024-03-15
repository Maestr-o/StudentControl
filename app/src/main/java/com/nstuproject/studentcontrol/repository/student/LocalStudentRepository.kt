package com.nstuproject.studentcontrol.repository.student

import com.nstuproject.studentcontrol.db.AppDb
import com.nstuproject.studentcontrol.db.entity.StudentEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalStudentRepository @Inject constructor(
    private val db: AppDb,
) : StudentRepository {

    override fun getAll(): Flow<List<StudentEntity>> =
        db.studentDao.getAll()

    override suspend fun save(data: StudentEntity) =
        db.studentDao.save(data)

    override suspend fun deleteById(id: Long) =
        db.studentDao.deleteById(id)
}