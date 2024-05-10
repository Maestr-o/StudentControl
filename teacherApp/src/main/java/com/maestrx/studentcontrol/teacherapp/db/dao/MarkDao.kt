package com.maestrx.studentcontrol.teacherapp.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.maestrx.studentcontrol.teacherapp.db.entity.MarkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MarkDao {

    @Query(
        """
        SELECT Mark.id, Mark.lesson_id, Mark.student_id
        FROM Mark, Lesson
        WHERE Mark.lesson_id == Lesson.id AND Mark.lesson_id == :lessonId
        """
    )
    fun getByLessonId(lessonId: Long): Flow<List<MarkEntity>>

    @Upsert
    suspend fun save(mark: MarkEntity)

    @Upsert
    suspend fun saveList(list: List<MarkEntity>)

    @Query(
        """
        SELECT Mark.id, Mark.lesson_id, Mark.student_id
        FROM Mark, `Group`, Student, Subject, LessonGroupCross, Lesson
        WHERE Subject.id == :subjectId AND `Group`.id == :groupId
            AND Mark.lesson_id == Lesson.id AND Lesson.subject_id == Subject.id
            AND Lesson.id == LessonGroupCross.lesson_id AND `Group`.id == LessonGroupCross.group_id
            AND Student.group_id == `Group`.id AND Student.id == Mark.student_id
        """
    )
    suspend fun getBySubjectIdAndGroupId(subjectId: Long, groupId: Long): List<MarkEntity>

    @Query(
        """
        SELECT COUNT(*) FROM Mark, Student, `Group`
        WHERE Mark.lesson_id == :lessonId AND Mark.student_id == Student.id
            AND `Group`.id == :groupId AND `Group`.id == Student.group_id
        """
    )
    suspend fun getCountByLessonIdAndGroupId(lessonId: Long, groupId: Long): Int

    @Query("SELECT COUNT(*) FROM Mark")
    suspend fun getCount(): Int
}