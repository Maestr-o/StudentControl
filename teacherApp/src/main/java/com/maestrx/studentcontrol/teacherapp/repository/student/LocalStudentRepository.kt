package com.maestrx.studentcontrol.teacherapp.repository.student

import com.maestrx.studentcontrol.teacherapp.db.AppDb
import com.maestrx.studentcontrol.teacherapp.db.entity.StudentEntity
import com.maestrx.studentcontrol.teacherapp.model.StudentResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalStudentRepository @Inject constructor(
    private val db: AppDb,
) : StudentRepository {

    override fun getStudentsByGroup(groupId: Long): Flow<List<StudentResponse>> =
        db.studentDao.getStudentsByGroup(groupId).map { list ->
            list.sortedBy {
                it.lastName
            }
        }

    override suspend fun save(data: StudentEntity) =
        db.studentDao.save(data)

    override suspend fun deleteById(id: Long) =
        db.studentDao.deleteById(id)

    override suspend fun getStudentIdByDeviceId(deviceId: String): Long =
        db.studentDao.getStudentIdByDeviceId(deviceId)

    override suspend fun saveDeviceId(studentId: Long, deviceId: String) =
        db.studentDao.saveDeviceId(studentId, deviceId)
}