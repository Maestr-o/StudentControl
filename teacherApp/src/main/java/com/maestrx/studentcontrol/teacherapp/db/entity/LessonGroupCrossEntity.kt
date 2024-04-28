package com.maestrx.studentcontrol.teacherapp.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "LessonGroupCross",
    foreignKeys = [
        ForeignKey(
            entity = LessonEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("lessonId"),
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = GroupEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("groupId"),
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [
        Index(value = ["lessonId", "groupId"], unique = true),
        Index(value = ["groupId"]),
    ]
)
data class LessonGroupCrossEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,
    @ColumnInfo(name = "lessonId")
    val lessonId: Long = 0L,
    @ColumnInfo(name = "groupId")
    val groupId: Long = 0L,
)