package com.nstuproject.studentcontrol.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.nstuproject.studentcontrol.db.entity.LessonGroupCrossRefEntity

@Dao
interface LessonGroupCrossRefDao {

    @Query("SELECT * FROM LessonGroupCrossRef")
    suspend fun getAll(): List<LessonGroupCrossRefEntity>

    @Upsert
    suspend fun save(data: LessonGroupCrossRefEntity)

    @Query("DELETE FROM LessonGroupCrossRef WHERE id=:id")
    suspend fun deleteById(id: Long)
}