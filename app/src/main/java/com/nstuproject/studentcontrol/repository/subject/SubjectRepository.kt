package com.nstuproject.studentcontrol.repository.subject

import com.nstuproject.studentcontrol.model.Subject

interface SubjectRepository {
    suspend fun getAll(): List<Subject>
    suspend fun save(data: Subject)
    suspend fun deleteById(id: Long)
}