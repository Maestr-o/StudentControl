package com.maestrx.studentcontrol.teacherapp.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.maestrx.studentcontrol.teacherapp.db.entity.StudentEntity
import com.maestrx.studentcontrol.teacherapp.model.StudentResponse
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Query(
        """
        SELECT Student.id, `Group`.id as groupId, `Group`.name as groupName, Student.firstName, Student.midName,
            Student.lastName, Student.deviceId
        FROM Student, `Group`
        WHERE Student.groupId == :groupId AND `Group`.id == :groupId
        """
    )
    fun getByGroupId(groupId: Long): Flow<List<StudentResponse>>

    @Upsert
    suspend fun save(data: StudentEntity)

    @Query("DELETE FROM Student WHERE id=:id")
    suspend fun deleteById(id: Long)

    @Query("SELECT id FROM Student WHERE deviceId = :deviceId")
    suspend fun getIdByDeviceId(deviceId: String): Long

    @Query("UPDATE Student SET deviceId = :deviceId WHERE id = :studentId")
    suspend fun saveDeviceId(studentId: Long, deviceId: String)

    @Query(
        """
        SELECT COUNT(*) FROM Student, `Group`
        WHERE Student.groupId == :groupId AND Student.groupId == `Group`.id
        """
    )
    suspend fun getCountByGroupId(groupId: Long): Int

    @Query(
        """
        SELECT `Group`.name as groupName, Student.* FROM Student, Lesson, Attendance, `Group`
        WHERE Lesson.id == :lessonId AND Attendance.lessonId == Lesson.id
            AND Attendance.studentId == Student.id AND `Group`.id == Student.groupId
        """
    )
    suspend fun getByLessonId(lessonId: Long): List<StudentResponse>
}