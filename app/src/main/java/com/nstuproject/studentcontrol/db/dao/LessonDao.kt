package com.nstuproject.studentcontrol.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.nstuproject.studentcontrol.db.entity.LessonEntity
import com.nstuproject.studentcontrol.model.LessonResponse

@Dao
interface LessonDao {

    @Query(
        """
        SELECT Lesson.id, Lesson.auditory, Lesson.description, Lesson.timeStart, Lesson.timeEnd,
            Lesson.title, Lesson.type, Subject.id as subjectId, Subject.name as subjectName
        FROM Lesson, Subject
        WHERE Subject.id = Lesson.subjectId AND Lesson.timeStart BETWEEN :startTime AND :endTime
        """
    )
    suspend fun getLessonsForPeriod(startTime: Long, endTime: Long): List<LessonResponse>

    @Upsert
    suspend fun save(data: LessonEntity)

    @Query("DELETE FROM Lesson WHERE id=:id")
    suspend fun deleteById(id: Long)
}