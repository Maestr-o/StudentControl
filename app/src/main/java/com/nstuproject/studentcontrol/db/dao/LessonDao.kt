package com.nstuproject.studentcontrol.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.nstuproject.studentcontrol.db.entity.LessonEntity
import com.nstuproject.studentcontrol.model.LessonResponse
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonDao {

    @Query(
        """
        SELECT Lesson.id, Lesson.auditory, Lesson.description, Lesson.datetime, Lesson.title, Lesson.type,
            Subject.id as subjectId, Subject.name as subjectName
        FROM Lesson, Subject
        WHERE Subject.id = Lesson.subjectId
        """
    )
    fun getLessons(): Flow<List<LessonResponse>>

    @Upsert
    suspend fun save(data: LessonEntity)

    @Query("DELETE FROM Lesson WHERE id=:id")
    suspend fun deleteById(id: Long)
}