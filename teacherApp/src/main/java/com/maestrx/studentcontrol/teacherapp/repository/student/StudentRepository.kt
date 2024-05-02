package com.maestrx.studentcontrol.teacherapp.repository.student

import com.maestrx.studentcontrol.teacherapp.db.entity.StudentEntity
import com.maestrx.studentcontrol.teacherapp.model.StudentResponse
import kotlinx.coroutines.flow.Flow

interface StudentRepository {
    fun getByGroupId(groupId: Long): Flow<List<StudentResponse>>
    suspend fun save(data: StudentEntity)
    suspend fun saveList(list: List<StudentEntity>)
    suspend fun deleteById(id: Long)
    suspend fun getIdByDeviceId(deviceId: String): Long
    suspend fun saveDeviceId(studentId: Long, deviceId: String)
    suspend fun getCountByGroupId(groupId: Long): Int
    suspend fun getAttendedByLessonId(lessonId: Long): List<StudentResponse>
    suspend fun getNotAttendedByLessonId(lessonId: Long): List<StudentResponse>
}