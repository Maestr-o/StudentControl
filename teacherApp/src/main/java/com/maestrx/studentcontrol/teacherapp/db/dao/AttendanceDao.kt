package com.maestrx.studentcontrol.teacherapp.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.maestrx.studentcontrol.teacherapp.db.entity.AttendanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {

    @Query("SELECT * FROM Attendance")
    fun getAll(): Flow<List<AttendanceEntity>>

    @Upsert
    suspend fun save(data: AttendanceEntity)

    @Query("DELETE FROM Attendance WHERE id=:id")
    suspend fun deleteById(id: Long)
}