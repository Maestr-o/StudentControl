package com.maestrx.studentcontrol.teacherapp.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Student",
    foreignKeys = [
        ForeignKey(
            entity = GroupEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("group_id"),
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["first_name", "mid_name", "last_name"], unique = true),
        Index(value = ["group_id"]),
    ]
)
data class StudentEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,
    @ColumnInfo(name = "group_id")
    val groupId: Long = 0L,
    @ColumnInfo(name = "first_name")
    val firstName: String = "",
    @ColumnInfo(name = "mid_name")
    val midName: String? = null,
    @ColumnInfo(name = "last_name")
    val lastName: String = "",
    @ColumnInfo(name = "device_id")
    val deviceId: String = "",
)