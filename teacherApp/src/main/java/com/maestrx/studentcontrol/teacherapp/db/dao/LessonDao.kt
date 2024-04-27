package com.maestrx.studentcontrol.teacherapp.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.maestrx.studentcontrol.teacherapp.db.entity.LessonEntity
import com.maestrx.studentcontrol.teacherapp.model.LessonResponse
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonDao {
    @Query(
        """
        SELECT Subject.id as subjectId, Subject.name as subjectName, Lesson.*
        FROM Lesson, Subject
        WHERE Subject.id = Lesson.subjectId AND Lesson.timeStart BETWEEN :startTime AND :endTime
        """
    )
    suspend fun getForPeriod(startTime: Long, endTime: Long): List<LessonResponse>

    @Upsert
    suspend fun save(data: LessonEntity): Long

    @Query("DELETE FROM Lesson WHERE id=:id")
    suspend fun deleteById(id: Long)

    @Query(
        """
        SELECT Subject.id as subjectId, Subject.name as subjectName, Lesson.*
        FROM Lesson, Subject, `Group`, LessonGroupCross
        WHERE LessonGroupCross.groupId == `Group`.id AND LessonGroupCross.lessonId == Lesson.id
            AND `Group`.id == :groupId AND Subject.id == :subjectId AND Lesson.subjectId == Subject.id
        """
    )
    suspend fun getBySubjectIdAndGroupId(subjectId: Long, groupId: Long): List<LessonResponse>

    @Query(
        """
        SELECT COUNT(*) FROM Lesson, Subject, `Group`, LessonGroupCross
        WHERE LessonGroupCross.groupId == `Group`.id AND LessonGroupCross.lessonId == Lesson.id
            AND `Group`.id == :groupId AND Subject.id == :subjectId AND Lesson.subjectId == Subject.id
        """
    )
    suspend fun getCountBySubjectIdAndGroupId(subjectId: Long, groupId: Long): Int

    @Query("SELECT COUNT(*) FROM Lesson")
    fun getCount(): Flow<Long>
}