package com.nstuproject.studentcontrol.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.nstuproject.studentcontrol.db.entity.LessonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonDao {

    @Query("SELECT * FROM Lesson")
    fun getAll(): Flow<List<LessonEntity>>

    @Upsert
    suspend fun save(data: LessonEntity)

    @Query("DELETE FROM Lesson WHERE id=:id")
    suspend fun deleteById(id: Long)
}