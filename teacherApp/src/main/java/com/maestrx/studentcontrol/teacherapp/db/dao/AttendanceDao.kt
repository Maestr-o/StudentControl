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
    fun getByLessonId(lessonId: Long): Flow<List<AttendanceEntity>>

    @Upsert
    suspend fun save(attendance: AttendanceEntity)

    @Upsert
    suspend fun saveList(list: List<AttendanceEntity>)

    @Query(
        """
        SELECT Attendance.id, Attendance.lessonId, Attendance.studentId
        FROM Attendance, `Group`, Student, Subject, LessonGroupCross, Lesson
        WHERE Subject.id == :subjectId AND `Group`.id == :groupId
            AND Attendance.lessonId == Lesson.id AND Lesson.subjectId == Subject.id
            AND Lesson.id == LessonGroupCross.lessonId AND `Group`.id == LessonGroupCross.groupId
            AND Student.groupId == `Group`.id AND Student.id == Attendance.studentId
        """
    )
    suspend fun getBySubjectIdAndGroupId(subjectId: Long, groupId: Long): List<AttendanceEntity>

    @Query(
        """
        SELECT COUNT(*) FROM Attendance, Student, `Group`
        WHERE Attendance.lessonId == :lessonId AND Attendance.studentId == Student.id
            AND `Group`.id == :groupId AND `Group`.id == Student.groupId
        """
    )
    suspend fun getCountByLessonIdAndGroupId(lessonId: Long, groupId: Long): Int

    @Query("SELECT COUNT(*) FROM Attendance")
    suspend fun getCount(): Int
}