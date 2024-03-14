package com.nstuproject.studentcontrol.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nstuproject.studentcontrol.model.Group

@Entity(tableName = "Group")
data class GroupEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,
    @ColumnInfo(name = "name")
    val name: String = "",
) {

    companion object {
        fun toEntity(data: Group): GroupEntity = with(data) {
            GroupEntity(id, name)
        }
    }

    fun toData(): Group = Group(id, name)
}