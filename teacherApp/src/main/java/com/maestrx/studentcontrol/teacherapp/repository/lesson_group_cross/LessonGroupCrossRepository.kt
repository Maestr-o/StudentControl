package com.maestrx.studentcontrol.teacherapp.repository.lesson_group_cross

import com.maestrx.studentcontrol.teacherapp.model.LessonGroupCross

interface LessonGroupCrossRepository {
    suspend fun save(data: List<LessonGroupCross>)
    suspend fun clear(lessonId: Long)
}