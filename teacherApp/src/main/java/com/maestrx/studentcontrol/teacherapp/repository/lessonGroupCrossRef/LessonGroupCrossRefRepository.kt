package com.maestrx.studentcontrol.teacherapp.repository.lessonGroupCrossRef

import com.maestrx.studentcontrol.teacherapp.db.entity.GroupEntity
import com.maestrx.studentcontrol.teacherapp.model.LessonGroupCrossRef
import kotlinx.coroutines.flow.Flow

interface LessonGroupCrossRefRepository {
    fun getGroupsByLesson(lessonId: Long): Flow<List<GroupEntity>>
    suspend fun save(data: List<LessonGroupCrossRef>)
    suspend fun clear(lessonId: Long)
}