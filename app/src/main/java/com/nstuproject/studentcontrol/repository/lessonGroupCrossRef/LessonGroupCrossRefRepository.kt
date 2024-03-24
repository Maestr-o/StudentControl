package com.nstuproject.studentcontrol.repository.lessonGroupCrossRef

import com.nstuproject.studentcontrol.db.entity.GroupEntity
import com.nstuproject.studentcontrol.model.LessonGroupCrossRef
import kotlinx.coroutines.flow.Flow

interface LessonGroupCrossRefRepository {
    fun getGroupsByLesson(lessonId: Long): Flow<List<GroupEntity>>
    suspend fun save(data: List<LessonGroupCrossRef>)
}