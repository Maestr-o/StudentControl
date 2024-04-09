package com.maestrx.studentcontrol.teacherapp.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.maestrx.studentcontrol.teacherapp.db.entity.GroupEntity
import com.maestrx.studentcontrol.teacherapp.db.entity.LessonGroupCrossRefEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonGroupCrossRefDao {

    @Query(
        """
        SELECT `Group`.id, `Group`.name
        FROM LessonGroupCrossRef, Lesson, `Group`
        WHERE Lesson.id == :lessonId AND LessonGroupCrossRef.lessonId == :lessonId
            AND LessonGroupCrossRef.groupId == `Group`.id
        """
    )
    fun getGroupsByLesson(lessonId: Long): Flow<List<GroupEntity>>

    @Upsert
    suspend fun save(data: LessonGroupCrossRefEntity)

    @Query("DELETE FROM LessonGroupCrossRef WHERE lessonId=:lessonId")
    suspend fun clear(lessonId: Long)
}