package com.nstuproject.studentcontrol.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.nstuproject.studentcontrol.db.entity.AttendanceEntity

@Dao
interface AttendanceDao {

    @Query("SELECT * FROM Attendance")
    suspend fun getAll(): List<AttendanceEntity>

    @Upsert
    suspend fun save(data: AttendanceEntity)

    @Query("DELETE FROM Attendance WHERE id=:id")
    suspend fun deleteById(id: Long)
}