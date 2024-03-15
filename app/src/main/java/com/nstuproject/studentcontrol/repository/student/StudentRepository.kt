package com.nstuproject.studentcontrol.repository.student

import com.nstuproject.studentcontrol.model.Student

interface StudentRepository {
    suspend fun getAll(): List<Student>
    suspend fun save(data: Student)
    suspend fun deleteById(id: Long)
}