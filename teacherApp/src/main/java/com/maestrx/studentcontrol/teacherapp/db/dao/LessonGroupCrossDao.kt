package com.maestrx.studentcontrol.teacherapp.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.maestrx.studentcontrol.teacherapp.db.entity.LessonGroupCrossEntity

@Dao
interface LessonGroupCrossDao {

    @Upsert
    suspend fun save(data: LessonGroupCrossEntity)

    @Query("DELETE FROM LessonGroupCross WHERE lessonId == :lessonId")
    suspend fun clear(lessonId: Long)
}