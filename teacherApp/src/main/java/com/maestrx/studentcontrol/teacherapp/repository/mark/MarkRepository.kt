package com.maestrx.studentcontrol.teacherapp.repository.mark

import com.maestrx.studentcontrol.teacherapp.db.entity.MarkEntity
import kotlinx.coroutines.flow.Flow

interface MarkRepository {
    fun getByLessonId(lessonId: Long): Flow<List<MarkEntity>>
    suspend fun save(data: MarkEntity)
    suspend fun saveList(list: List<MarkEntity>)
    suspend fun getBySubjectIdAndGroupId(subjectId: Long, groupId: Long): List<MarkEntity>
    suspend fun getCountByLessonIdAndGroupId(lessonId: Long, groupId: Long): Int
    suspend fun getCount(): Int
    suspend fun getByStudentIdAndSubjectId(studentId: Long, subjectId: Long): List<MarkEntity>
    suspend fun delete(mark: MarkEntity)
}