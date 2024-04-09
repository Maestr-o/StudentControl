package com.maestrx.studentcontrol.teacherapp.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.maestrx.studentcontrol.teacherapp.db.entity.SubjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {

    @Query("SELECT * FROM Subject")
    fun getAll(): Flow<List<SubjectEntity>>

    @Upsert
    suspend fun save(data: SubjectEntity)

    @Query("DELETE FROM Subject WHERE id=:id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM Subject")
    fun getCount(): Flow<Long>
}