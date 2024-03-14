package com.nstuproject.studentcontrol.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.nstuproject.studentcontrol.db.entity.StudentEntity

@Dao
interface StudentDao {

    @Query("SELECT * FROM Student")
    suspend fun getAll(): List<StudentEntity>

    @Upsert
    suspend fun save(data: StudentEntity)

    @Query("DELETE FROM Student WHERE id=:id")
    suspend fun deleteById(id: Long)
}