package com.nstuproject.studentcontrol.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Attendance",
    foreignKeys = [
        ForeignKey(
            entity = LessonEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("lessonId"),
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = StudentEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("studentId"),
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,
    @ColumnInfo(name = "lessonId")
    val lessonId: Long = 0L,
    @ColumnInfo(name = "studentId")
    val studentId: Long = 0L,
    @ColumnInfo(name = "attended")
    val attended: Boolean = false,
)