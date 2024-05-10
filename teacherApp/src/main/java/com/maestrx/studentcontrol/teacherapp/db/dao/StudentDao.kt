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
        SELECT Student.id, `Group`.id as groupId, `Group`.name as groupName,
            Student.first_name as firstName, Student.mid_name as midName,
            Student.last_name as lastName, Student.device_id as deviceId
        FROM Student, `Group`
        WHERE Student.group_id == :groupId AND `Group`.id == :groupId
        """
    )
    fun getByGroupId(groupId: Long): Flow<List<StudentResponse>>

    @Upsert
    suspend fun save(data: StudentEntity)

    @Upsert
    suspend fun saveList(list: List<StudentEntity>)

    @Query("DELETE FROM Student WHERE id=:id")
    suspend fun deleteById(id: Long)

    @Query("SELECT id FROM Student WHERE device_id = :deviceId")
    suspend fun getIdByDeviceId(deviceId: String): Long

    @Query("UPDATE Student SET device_id = :deviceId WHERE id = :studentId")
    suspend fun saveDeviceId(studentId: Long, deviceId: String)

    @Query(
        """
        SELECT COUNT(*) FROM Student, `Group`
        WHERE Student.group_id == :groupId AND Student.group_id == `Group`.id
        """
    )
    suspend fun getCountByGroupId(groupId: Long): Int

    @Query(
        """
        SELECT `Group`.name as groupName, Student.id, Student.group_id as groupId,
            Student.device_id as deviceId, Student.first_name as firstName,
            Student.mid_name as midName, Student.last_name as lastName
        FROM Student, Lesson, Mark, `Group`, LessonGroupCross
        WHERE Lesson.id == :lessonId AND Mark.lesson_id == Lesson.id
            AND Mark.student_id == Student.id AND `Group`.id == Student.group_id
            AND LessonGroupCross.lesson_id == Lesson.id AND LessonGroupCross.group_id == `Group`.id
        """
    )
    suspend fun getMarkedByLessonId(lessonId: Long): List<StudentResponse>

    @Query(
        """
        SELECT `Group`.name as groupName, Student.id, Student.group_id as groupId,
            Student.device_id as deviceId, Student.first_name as firstName,
            Student.mid_name as midName, Student.last_name as lastName
        FROM Student, Lesson, `Group`, LessonGroupCross
        WHERE Lesson.id == :lessonId AND `Group`.id == Student.group_id
            AND LessonGroupCross.lesson_id == Lesson.id AND LessonGroupCross.group_id == `Group`.id
        AND NOT EXISTS (
            SELECT 1 FROM Mark 
            WHERE Mark.lesson_id == Lesson.id AND Mark.student_id == Student.id
        )
        """
    )
    suspend fun getNotMarkedByLessonId(lessonId: Long): List<StudentResponse>
}