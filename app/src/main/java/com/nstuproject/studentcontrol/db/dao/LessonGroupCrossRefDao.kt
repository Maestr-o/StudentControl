package com.nstuproject.studentcontrol.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.nstuproject.studentcontrol.db.entity.GroupEntity
import com.nstuproject.studentcontrol.db.entity.LessonGroupCrossRefEntity
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

    @Query("DELETE FROM LessonGroupCrossRef WHERE id=:id")
    suspend fun deleteById(id: Long)
}