package com.maestrx.studentcontrol.teacherapp.repository.student

import com.maestrx.studentcontrol.teacherapp.db.dao.StudentDao
import com.maestrx.studentcontrol.teacherapp.db.entity.StudentEntity
import com.maestrx.studentcontrol.teacherapp.model.StudentResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalStudentRepository @Inject constructor(
    private val studentDao: StudentDao,
) : StudentRepository {

    override fun getByGroupId(groupId: Long): Flow<List<StudentResponse>> =
        studentDao.getByGroupId(groupId).map { list ->
            list.sortedBy {
                it.lastName
            }
        }

    override suspend fun save(data: StudentEntity) =
        studentDao.save(data)

    override suspend fun deleteById(id: Long) =
        studentDao.deleteById(id)

    override suspend fun getIdByDeviceId(deviceId: String): Long =
        studentDao.getIdByDeviceId(deviceId)

    override suspend fun saveDeviceId(studentId: Long, deviceId: String) =
        studentDao.saveDeviceId(studentId, deviceId)

    override suspend fun getCountByGroupId(groupId: Long): Int =
        studentDao.getCountByGroupId(groupId)

    override suspend fun getByLessonId(lessonId: Long): List<StudentResponse> =
        studentDao.getByLessonId(lessonId)
            .sortedWith(compareBy({ it.groupName }, { it.lastName }))
}