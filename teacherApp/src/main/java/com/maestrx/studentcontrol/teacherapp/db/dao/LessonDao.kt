package com.maestrx.studentcontrol.teacherapp.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.maestrx.studentcontrol.teacherapp.db.entity.LessonEntity
import com.maestrx.studentcontrol.teacherapp.model.LessonResponse
import com.maestrx.studentcontrol.teacherapp.model.StudentResponse
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
    suspend fun getLessonsForPeriod(startTime: Long, endTime: Long): List<LessonResponse>

    @Query(
        """
        SELECT `Group`.name as groupName, Student.* FROM Student, Lesson, Attendance, `Group`
        WHERE Lesson.id == :lessonId AND Attendance.lessonId == Lesson.id
            AND Attendance.studentId == Student.id AND `Group`.id == Student.groupId
        """
    )
    suspend fun getStudentsByLessonId(lessonId: Long): List<StudentResponse>

    @Upsert
    suspend fun save(data: LessonEntity): Long

    @Query("DELETE FROM Lesson WHERE id=:id")
    suspend fun deleteById(id: Long)

    @Query(
        """
        SELECT Subject.id as subjectId, Subject.name as subjectName, Lesson.*
        FROM Lesson, Subject, `Group`, LessonGroupCrossRef
        WHERE LessonGroupCrossRef.groupId == `Group`.id AND LessonGroupCrossRef.lessonId == Lesson.id
            AND `Group`.id == :groupId AND Subject.id == :subjectId AND Lesson.subjectId == Subject.id
        """
    )
    suspend fun getLessonsBySubjectAndGroup(subjectId: Long, groupId: Long): List<LessonResponse>

    @Query(
        """
        SELECT COUNT(*) FROM Lesson, Subject, `Group`, LessonGroupCrossRef
        WHERE LessonGroupCrossRef.groupId == `Group`.id AND LessonGroupCrossRef.lessonId == Lesson.id
            AND `Group`.id == :groupId AND Subject.id == :subjectId AND Lesson.subjectId == Subject.id
        """
    )
    suspend fun getCountBySubjectAndGroup(subjectId: Long, groupId: Long): Int

    @Query("SELECT COUNT(*) FROM Lesson")
    fun getCount(): Flow<Long>
}