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
        SELECT Subject.id as subjectId, Subject.name as subjectName, Lesson.id,
            Lesson.time_start as timeStart, Lesson.time_end as timeEnd, Lesson.title,
            Lesson.type, Lesson.auditory, Lesson.description
        FROM Lesson, Subject
        WHERE Subject.id = Lesson.subject_id AND Lesson.time_start BETWEEN :startTime AND :endTime
        """
    )
    suspend fun getForPeriod(startTime: Long, endTime: Long): List<LessonResponse>

    @Upsert
    suspend fun save(data: LessonEntity): Long

    @Query("DELETE FROM Lesson WHERE id=:id")
    suspend fun deleteById(id: Long)

    @Query(
        """
        SELECT Subject.id as subjectId, Subject.name as subjectName, Lesson.id,
            Lesson.time_start as timeStart, Lesson.time_end as timeEnd, Lesson.title,
            Lesson.type, Lesson.auditory, Lesson.description
        FROM Lesson, Subject, `Group`, LessonGroupCross
        WHERE LessonGroupCross.group_id == `Group`.id AND LessonGroupCross.lesson_id == Lesson.id
            AND `Group`.id == :groupId AND Subject.id == :subjectId AND Lesson.subject_id == Subject.id
            AND Lesson.time_start <= :timeStart
        """
    )
    suspend fun getBySubjectIdAndGroupIdAndStartTime(
        subjectId: Long,
        groupId: Long,
        timeStart: Long
    ): List<LessonResponse>

    @Query(
        """
        SELECT COUNT(*) FROM Lesson, Subject, `Group`, LessonGroupCross
        WHERE LessonGroupCross.group_id == `Group`.id AND LessonGroupCross.lesson_id == Lesson.id
            AND `Group`.id == :groupId AND Subject.id == :subjectId AND Lesson.subject_id == Subject.id
            AND Lesson.time_start <= :timeStart
        """
    )
    suspend fun getCountBySubjectIdAndGroupIdAndStartTime(
        subjectId: Long,
        groupId: Long,
        timeStart: Long
    ): Int

    @Query("SELECT COUNT(*) FROM Lesson")
    fun getCount(): Flow<Long>
}