package com.nstuproject.studentcontrol.repository.student

import com.nstuproject.studentcontrol.db.entity.StudentEntity
import com.nstuproject.studentcontrol.model.StudentResponse
import kotlinx.coroutines.flow.Flow

interface StudentRepository {
    fun getStudentsByGroup(groupId: Long): Flow<List<StudentResponse>>
    suspend fun save(data: StudentEntity)
    suspend fun deleteById(id: Long)
}