package com.nstuproject.studentcontrol.repository.student

import com.nstuproject.studentcontrol.db.AppDb
import com.nstuproject.studentcontrol.db.entity.StudentEntity
import com.nstuproject.studentcontrol.model.Student
import javax.inject.Inject

class LocalStudentRepository @Inject constructor(
    private val db: AppDb,
) : StudentRepository {

    override suspend fun getAll(): List<Student> =
        db.studentDao.getAll().map {
            it.toData()
        }

    override suspend fun save(data: Student) =
        db.studentDao.save(StudentEntity.toEntity(data))

    override suspend fun deleteById(id: Long) =
        db.studentDao.deleteById(id)
}