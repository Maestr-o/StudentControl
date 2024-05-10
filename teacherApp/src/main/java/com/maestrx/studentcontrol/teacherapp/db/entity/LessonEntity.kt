package com.maestrx.studentcontrol.teacherapp.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.maestrx.studentcontrol.teacherapp.model.LessonType

@Entity(
    tableName = "Lesson",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("subject_id"),
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [
        Index(value = ["subject_id"]),
    ]
)
data class LessonEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,
    @ColumnInfo(name = "title")
    val title: String = "",
    @ColumnInfo(name = "time_start")
    val timeStart: Long = 0L,
    @ColumnInfo(name = "time_end")
    val timeEnd: Long = 0L,
    @ColumnInfo(name = "subject_id")
    val subjectId: Long = 0L,
    @ColumnInfo(name = "auditory")
    val auditory: String = "",
    @ColumnInfo(name = "description")
    val description: String = "",
    @ColumnInfo(name = "type")
    val type: LessonType = LessonType.LECTURE,
)