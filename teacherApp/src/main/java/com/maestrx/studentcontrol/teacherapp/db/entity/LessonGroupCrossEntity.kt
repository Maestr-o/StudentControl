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
            childColumns = arrayOf("lesson_id"),
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = GroupEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("group_id"),
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [
        Index(value = ["lesson_id", "group_id"], unique = true),
        Index(value = ["group_id"]),
    ]
)
data class LessonGroupCrossEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,
    @ColumnInfo(name = "lesson_id")
    val lessonId: Long = 0L,
    @ColumnInfo(name = "group_id")
    val groupId: Long = 0L,
)