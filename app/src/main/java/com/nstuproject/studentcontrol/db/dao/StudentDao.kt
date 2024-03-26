package com.nstuproject.studentcontrol.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.nstuproject.studentcontrol.db.entity.StudentEntity
import com.nstuproject.studentcontrol.model.StudentResponse
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
    fun getStudentsByGroup(groupId: Long): Flow<List<StudentResponse>>

    @Upsert
    suspend fun save(data: StudentEntity)

    @Query("DELETE FROM Student WHERE id=:id")
    suspend fun deleteById(id: Long)
}