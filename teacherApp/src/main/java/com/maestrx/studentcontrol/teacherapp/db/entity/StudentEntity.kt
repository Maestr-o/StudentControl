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
            childColumns = arrayOf("groupId"),
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["firstName", "midName", "lastName"], unique = true)
    ]
)
data class StudentEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,
    @ColumnInfo(name = "groupId")
    val groupId: Long = 0L,
    @ColumnInfo(name = "firstName")
    val firstName: String = "",
    @ColumnInfo(name = "midName")
    val midName: String? = null,
    @ColumnInfo(name = "lastName")
    val lastName: String = "",
    @ColumnInfo(name = "deviceId")
    val deviceId: String = "",
)