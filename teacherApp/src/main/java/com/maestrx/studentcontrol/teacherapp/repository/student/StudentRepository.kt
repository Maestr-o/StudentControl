package com.maestrx.studentcontrol.teacherapp.repository.student

import com.maestrx.studentcontrol.teacherapp.db.entity.StudentEntity
import com.maestrx.studentcontrol.teacherapp.model.StudentResponse
import kotlinx.coroutines.flow.Flow

interface StudentRepository {
    fun getStudentsByGroup(groupId: Long): Flow<List<StudentResponse>>
    suspend fun save(data: StudentEntity)
    suspend fun deleteById(id: Long)
    suspend fun getStudentIdByDeviceId(deviceId: String): Long
    suspend fun saveDeviceId(studentId: Long, deviceId: String)
    suspend fun getStudentsCountByGroup(groupId: Long): Int
}