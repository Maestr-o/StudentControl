package com.maestrx.studentcontrol.teacherapp.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.maestrx.studentcontrol.teacherapp.db.entity.AttendanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {

    @Query(
        """
        SELECT Attendance.id, Attendance.lessonId, Attendance.studentId
        FROM Attendance, Lesson
        WHERE Attendance.lessonId == Lesson.id AND Attendance.lessonId == :lessonId
        """
    )
    fun getByLesson(lessonId: Long): Flow<List<AttendanceEntity>>

    @Upsert
    suspend fun save(data: AttendanceEntity)

    @Query(
        """
        SELECT Attendance.id, Attendance.lessonId, Attendance.studentId
        FROM Attendance, `Group`, Student, Subject
        WHERE Subject.id == :subjectId AND `Group`.id == :groupId
            AND Student.groupId == `Group`.id AND Student.id == Attendance.studentId
        """
    )
    suspend fun getBySubjectAndGroup(subjectId: Long, groupId: Long): List<AttendanceEntity>

    @Query(
        """
        SELECT COUNT(*) FROM Attendance, Student, `Group`
        WHERE Attendance.lessonId == :lessonId AND Attendance.studentId == Student.id
            AND `Group`.id == :groupId AND `Group`.id == Student.groupId
        """
    )
    suspend fun getCountByLessonAndGroup(lessonId: Long, groupId: Long): Int

    @Query("SELECT COUNT(*) FROM Attendance")
    suspend fun getCount(): Int
}