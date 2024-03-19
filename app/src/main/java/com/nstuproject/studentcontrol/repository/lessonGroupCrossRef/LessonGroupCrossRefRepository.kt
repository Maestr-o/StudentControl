package com.nstuproject.studentcontrol.repository.lessonGroupCrossRef

import com.nstuproject.studentcontrol.db.entity.GroupEntity
import com.nstuproject.studentcontrol.db.entity.LessonEntity
import kotlinx.coroutines.flow.Flow

interface LessonGroupCrossRefRepository {
    fun getGroupsByLesson(lessonId: Long): Flow<List<GroupEntity>>
    suspend fun save(data: LessonEntity)
    suspend fun deleteById(id: Long)
}