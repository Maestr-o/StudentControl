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
}