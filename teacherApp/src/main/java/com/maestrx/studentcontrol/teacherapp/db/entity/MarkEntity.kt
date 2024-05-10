package com.maestrx.studentcontrol.teacherapp.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Mark",
    foreignKeys = [
        ForeignKey(
            entity = LessonEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("lesson_id"),
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = StudentEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("student_id"),
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["lesson_id", "student_id"], unique = true),
        Index(value = ["student_id"]),
    ]
)
data class MarkEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,
    @ColumnInfo(name = "lesson_id")
    val lessonId: Long = 0L,
    @ColumnInfo(name = "student_id")
    val studentId: Long = 0L,
)