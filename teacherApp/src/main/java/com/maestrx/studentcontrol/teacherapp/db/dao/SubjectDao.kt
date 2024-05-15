package com.maestrx.studentcontrol.teacherapp.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.maestrx.studentcontrol.teacherapp.db.entity.SubjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {

    @Query("SELECT * FROM Subject")
    fun getAll(): Flow<List<SubjectEntity>>

    @Upsert
    suspend fun save(data: SubjectEntity)

    @Query("DELETE FROM Subject WHERE id=:id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM Subject")
    fun getCount(): Flow<Long>

    @Query(
        """
        SELECT DISTINCT Subject.* FROM Subject, `Group`, Lesson, LessonGroupCross
        WHERE Subject.id == Lesson.subject_id AND Lesson.id == LessonGroupCross.lesson_id
            AND LessonGroupCross.group_id == `Group`.id AND `Group`.id == :groupId
    """
    )
    suspend fun getByGroupId(groupId: Long): List<SubjectEntity>
}