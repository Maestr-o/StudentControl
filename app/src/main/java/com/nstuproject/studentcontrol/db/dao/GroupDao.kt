package com.nstuproject.studentcontrol.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.nstuproject.studentcontrol.db.entity.GroupEntity

@Dao
interface GroupDao {

    @Query("SELECT * FROM `Group`")
    suspend fun getAll(): List<GroupEntity>

    @Upsert
    suspend fun save(data: GroupEntity)

    @Query("DELETE FROM `Group` WHERE id=:id")
    suspend fun deleteById(id: Long)
}